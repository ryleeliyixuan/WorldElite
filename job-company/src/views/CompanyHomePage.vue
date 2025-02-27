<template>
    <div class="app-container container">
        <b-media class="mt-4" vertical-align="center" v-if="company">
            <template v-slot:aside>
                <b-img :src="company.logo" class="company-logo" :alt="company.fullName" v-if="company.logo"></b-img>
                <b-img blank blank-color="#ccc" class="company-logo" v-else></b-img>
            </template>
            <b-media-body>
                <b-row align-v="center">
                    <b-col>
                        <h5 class="mt-0">{{company.name}}</h5>
                        <p class="mb-0">
                            <span v-if="company.stage">{{company.stage.name}} .</span>
                            <span v-if="company.property">{{company.property.name}} .</span>
                            <span v-if="company.industry">{{company.industry.name}} .</span>
                            <span v-if="company.scale">{{company.scale.name}}</span>
                        </p>
                        <div class="mt-2 mb-0" v-if="company.homepage">
                            <el-link :href="companyLink" target="_blank" :underline="false">
                                公司官网
                                <i class="el-icon-position el-icon--right"></i>
                            </el-link>
                        </div>
                    </b-col>
                    <b-col cols="4">
                    </b-col>
                </b-row>
            </b-media-body>
        </b-media>
        <el-menu :default-active="tabIndex" mode="horizontal" @select="handleSelectTab">
            <el-menu-item index="intro">公司介绍</el-menu-item>
            <el-menu-item index="job">招聘岗位</el-menu-item>
        </el-menu>
        <div class="mt-4" v-if="company">
            <div class="intro-box" v-if="tabIndex == 'intro'">
                <div v-if="company.jobList && company.jobList.length !== 0">
                    <h5 class="mt-4 mb-4">
                        热招职位
                        <el-link :underline="false" class="float-right" @click="handleSelectTab('job')">
                            查看全部
                            <i class="el-icon-arrow-right el-icon--right"></i>
                        </el-link>
                    </h5>
                    <div class="recommend-jobs mb-4">
                        <el-row :gutter="12">
                            <el-col :span="8" v-for="job in company.jobList" :key="job.id">
                                <el-card shadow="hover" @click.native="onJobClick(job)" class="link-pointer">
                                    <h6>
                                        {{job.name}}
                                        <span
                                                class="text-danger float-right"
                                        >{{job.minSalary}}K - {{job.maxSalary}}K</span>
                                    </h6>
                                    <div
                                            class="text-small text-gray"
                                    >{{job.minDegree? job.minDegree.name: ''}} . {{job.city? job.city.name: ''}}
                                    </div>
                                </el-card>
                            </el-col>
                        </el-row>
                    </div>
                </div>
                <h5 class="mt-4 mb-4" v-if="company.introduction">公司简介</h5>
                <div class="introdution" v-html="company.introduction"></div>
                <h5 class="mt-4 mb-4" v-if="company.addressList && company.addressList.length != 0">公司地址</h5>
                <el-collapse
                        v-model="activeAddress"
                        accordion
                        v-for="(addr, index) in company.addressList"
                        :key="addr.id"
                >
                    <el-collapse-item :title="addr.address" :name="index">
                        <div class="map-box">
                            <el-amap :vid="'amap' + index" :zoom="mapZoom" :center="addr.mapWindow.position">
                                <el-amap-info-window
                                        :position="addr.mapWindow.position"
                                        :content="addr.mapWindow.content"
                                ></el-amap-info-window>
                            </el-amap>
                        </div>
                    </el-collapse-item>
                </el-collapse>
                <div v-if="company.companyWiki">
                    <h5 class="mt-4 mb-4">公司百科</h5>
                    <div class="introdution ql-editor" v-html="company.companyWiki"></div>
                </div>
            </div>
            <div class="row job-box" v-else>
                <div class="col-lg-8 col-md-10 col-sm-12">
                    <el-card
                            shadow="hover"
                            v-for="job in jobPage.list"
                            :key="job.id"
                            class="mb-2 link-pointer"
                            @click.native="onJobClick(job)">
                        <h6>
                            {{job.name}}
                            <span
                                    class="text-danger float-right"
                            >{{job.minSalary}}K - {{job.maxSalary}}K</span>
                        </h6>
                        <div
                                class="text-small text-gray"
                        >{{job.minDegree? job.minDegree.name: ''}} . {{job.city? job.city.name: ''}}
                        </div>
                    </el-card>
                    <pagination
                            v-show="total"
                            :total="total"
                            :page.sync="listQuery.page"
                            :limit.sync="listQuery.limit"
                            @pagination="getCompanyJobList"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import Vue from "vue";
    import VueAMap from "vue-amap";
    import Pagination from "@/components/Pagination";

    import {getCompanyInfo} from "@/api/company_api";
    import {getCompanyJobList} from "@/api/job_api";

    Vue.use(VueAMap);

    VueAMap.initAMapApiLoader({
        key: process.env.VUE_APP_AMAP_KEY,
        v: "1.4.4"
    });

    export default {
        name: "CompanyHomePage",
        components: {Pagination},
        data() {
            return {
                companyId: undefined,
                company: undefined,
                total: 0,
                jobPage: {},
                tabIndex: "intro",
                activeAddress: 0,
                mapZoom: 14,
                listQuery: {
                    companyId: undefined,
                    page: 1,
                    limit: 10,
                    sort: "-pub_time"
                },
                favoriteForm: {
                    objectId: undefined,
                    type: 2,
                    favorite: false
                },
                favoriteLoading: false,
            };
        },
        created() {
            this.initData();
        },
        computed: {
            companyLink() {
                return this.company.homepage.startsWith("http")
                    ? this.company.homepage
                    : "http://" + this.company.homepage;
            }
        },
        watch: {
            $route() {
                this.initData();
            }
        },
        methods: {
            initData() {
                this.companyId = this.$route.params.id;
                this.listQuery.companyId = this.companyId;
                this.favoriteForm.objectId = this.companyId;
                getCompanyInfo(this.companyId).then(response => {
                    this.company = response.data;
                    if (this.company.addressList) {
                        for (const addr of this.company.addressList) {
                            addr.mapWindow = {
                                position: [addr.longitude, addr.latitude],
                                content: addr.address
                            };
                        }
                    }
                });
                this.getCompanyJobList();
            },
            getCompanyJobList() {
                getCompanyJobList(this.listQuery).then(response => {
                    this.jobPage = response.data;
                    this.total = response.data.total;
                });
            },
            handleSelectTab(tabIndex) {
                this.tabIndex = tabIndex;
                if (tabIndex == "intro") {
                    this.listQuery.page = 1;
                    this.getCompanyJobList();
                }
            },
            onJobClick(job) {
                window.open(`${process.env.VUE_APP_WEB_HOST}/job/${job.id}`);
            }
        }
    };
</script>

<style scoped>
    .app-container {
        min-height: calc(100vh - 430px);
    }

    .company-logo {
        width: 100px;
        height: 100px;
    }

    .map-box {
        height: 200px;
    }

    .app-container {
        margin: 0 auto;
    }
</style>
