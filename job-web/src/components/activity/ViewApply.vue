<template>
    <el-dialog class="dialog"
               :visible.sync="dialogVisible"
               v-if="applyInfo"
               width="618px"
               :show-close="false">
        <div slot="title" class="dialog-title" v-if="applyInfo">
            <div class="title-left">
                <svg-icon :icon-class="index===1?'left-icon-disabled':'left-icon'" class="left-icon" clickable @click="onLeft"/>
                <svg-icon :icon-class="index===total?'right-icon-disabled':'right-icon'" class="right-icon" clickable @click="onRight"/>
                <div class="button button1-1" v-if="activity.auditType==='0' && applyInfo.status === 1" @click.stop="onResolve1">通过报名</div>
                <div class="button button1-2" v-if="activity.auditType==='0' && applyInfo.status === 1" @click.stop="onReject1">不合适</div>
                <div class="button button2-1" style="cursor: default;" v-if="activity.auditType==='0' && applyInfo.status === 2" @click.stop="">已通过</div>
                <!--                <div class="button button2-2" v-if="activity.auditType==='0' && applyInfo.status === 2" @click.stop="onReject2">不合适</div>-->
                <!--                <div class="button button3-1" v-if="activity.auditType==='0' && applyInfo.status === 3" @click.stop="onResolve3">重新通过</div>-->
                <div class="button button3-2" style="cursor: default;" v-if="activity.auditType==='0' && applyInfo.status === 3" @click.stop="">不合适</div>
            </div>
            <div class="title-right">
                <svg-icon icon-class="preview-icon" class="preview-icon" clickable v-if="activity.needResume === '1'" @click="onViewResume"/>
                <svg-icon icon-class="download-icon" class="download-icon" clickable @click="onLoadApplyTable"/>
                <svg-icon icon-class="close-icon" class="close-icon" @click="onDialogClose"/>
            </div>
        </div>

        <div class="content-container" v-if="applyInfo">
            <div class="title-container">
                <div class="number">{{number}}</div>
                <div class="title">各地留学生报名分享会报名表</div>
            </div>
            <div class="form-container">
                <div class="item"><span class="required">*</span> <span class="name">姓名：</span> <span class="value">{{applyInfo.name}}</span></div>
                <div class="item" v-if="applyInfo.genderFlag!=='2'">
                    <span class="required">{{applyInfo.genderFlag==='0'?'*':''}}</span>
                    <span class="name">性别：</span>
                    <span class="value">{{applyInfo.gender==='1'?'男':applyInfo.gender==='2'?'女':''}}</span>
                </div>
                <div class="item" v-if="applyInfo.phoneFlag!=='2'">
                    <span class="required">{{applyInfo.phoneFlag==='0'?'*':''}}</span>
                    <span class="name">手机：</span>
                    <span class="value">{{applyInfo.phone}}</span>
                </div>
                <div class="item" v-if="applyInfo.emailFlag!=='2'">
                    <span class="required">{{applyInfo.emailFlag==='0'?'*':''}}</span>
                    <span class="name">邮箱：</span>
                    <span class="value">{{applyInfo.email}}</span>
                </div>
                <div class="item" v-if="applyInfo.schoolFlag!=='2'">
                    <span class="required">{{applyInfo.schoolFlag==='0'?'*':''}}</span>
                    <span class="name">学校：</span>
                    <span class="value">{{applyInfo.school}}</span>
                </div>
                <div class="item" v-if="applyInfo.professionFlag!=='2'">
                    <span class="required">{{applyInfo.professionFlag==='0'?'*':''}}</span>
                    <span class="name">专业：</span>
                    <span class="value">{{applyInfo.profession}}</span>
                </div>
                <div class="item" v-if="applyInfo.educationFlag!=='2'">
                    <span class="required">{{applyInfo.educationFlag==='0'?'*':''}}</span>
                    <span class="name">学历：</span>
                    <span class="value">{{applyInfo.education && applyInfo.education.name}}</span>
                </div>
                <div class="question-container" v-for="(question, index) in applyInfo.questionnaireList" :key="index">
                    <div v-if="question.type==='1'" class="question1-container">
                        <div class="question1-line1">
                            <div class="title"><span class="must" v-if="question.mustAnswer==='1'">*</span>{{question.title}}</div>
                        </div>
                        <div class="question1-line2">
                            <div class="answer">{{question.answer}}</div>
                        </div>
                    </div>
                    <div v-else-if="question.type==='2'" class="question2-container">
                        <div class="question2-line1">
                            <div class="title"><span class="must" v-if="question.mustAnswer==='1'">*</span>{{question.title}}</div>
                        </div>
                        <div class="question2-line2">
                            <div v-for="(option, index) in question.questionnaireOptions" class="option-container" :key="index">
                                <svg-icon :icon-class="option.checked?'apply-table-selected':'apply-table-unselected'" class-name="icon"></svg-icon>
                                <div class="options">{{option.options}}</div>
                            </div>
                        </div>
                    </div>
                    <div v-else-if="question.type==='3'" class="question3-container">
                        <div class="question3-line1">
                            <div class="title"><span class="must" v-if="question.mustAnswer==='1'">*</span>{{question.title}}</div>
                        </div>
                        <div class="question3-line2">
                            <div v-for="(option, index) in question.questionnaireOptions" class="option-container" :key="index">
                                <svg-icon :icon-class="option.checked?'apply-table-checked':'apply-table-unchecked'" class-name="icon"></svg-icon>
                                <div class="options">{{option.options}}</div>
                            </div>
                        </div>
                    </div>
                    <div v-else-if="question.type==='4'" class="question4-container">
                        <div class="question4-line1">
                            <div class="title"><span class="must" v-if="question.mustAnswer==='1'">*</span>{{question.title}}</div>
                        </div>
                        <div class="question4-line2">
                            <el-image v-if="isPicture(question.annexUrl)" class="image" :src="question.annexUrl" fit="contain"
                                      :preview-src-list="[question.annexUrl]"></el-image>
                            <el-link v-else :href="question.annexUrl" class="link" target="_blanl">
                                {{question.annexUrl}}
                            </el-link>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <el-dialog class="cancel-dialog"
                   title="是否同时下载简历？"
                   :center="true"
                   :visible.sync="loadApplyTableDialogVisible"
                   append-to-body
                   width="445px">
            <el-checkbox v-model="andLoadingResumeNoTips">下载该活动报名表时不再提示</el-checkbox>
            <div class="button-container">
                <div class="cancel" @click="onLoadApplyTableOnly">否</div>
                <div class="confirm" @click="onLoadApplyTableAndResume">是</div>
            </div>
        </el-dialog>

        <el-dialog :visible.sync="resumeDialogVisible" width="750px" :show-close="false" class="load-resume-dialog" append-to-body>
            <div slot="title" class="dialog-title">
                <svg-icon icon-class="download-icon" class="download-icon" clickable @click="onLoadResume"/>
                <svg-icon icon-class="close-icon" class="close-icon" @click="resumeDialogVisible = false;"/>
            </div>
            <ResumeView :resumeDetail="resumeDetail"></ResumeView>

            <el-dialog class="resume-cancel-dialog"
                       title="是否同时下载报名表？"
                       :center="true"
                       :visible.sync="loadResumeDialogVisible"
                       append-to-body
                       width="445px">
                <el-checkbox v-model="andLoadingApplyTableNoTips">下载该活动简历时不再提示</el-checkbox>
                <div class="resume-button-container">
                    <div class="cancel" @click="onLoadResumeOnly">否</div>
                    <div class="confirm" @click="onLoadResumeAndResume">是</div>
                </div>
            </el-dialog>
        </el-dialog>
        <notice-dialog v-if="noticeDialogVisible" :visible.sync="noticeDialogVisible" :activityId="activity.id"
                       @confirm="applyPass"></notice-dialog>
    </el-dialog>
</template>

<script>

    import {downloadFile} from "@/utils/common";
    import ResumeView from "@/components/ResumeView";
    import NoticeDialog from "@/components/activity/NoticeDialog";
    import {getResumeDetail} from "@/api/resume_api";

    export default {
        name: "ViewApply",
        components: {ResumeView, NoticeDialog},
        props: {
            visible: {
                type: Boolean
            },
            activity: {
                type: Object
            },
            data: {
                type: Object
            },
        },
        computed: {
            dialogVisible: {
                get() {
                    return this.visible
                },
                set(val) {
                    this.$emit('update:visible', val)
                }
            },
            number() {
                return "No." + ("0000" + this.index).substr(-4);
            }
        },
        data() {
            return {
                applyInfo: undefined, // 报名信息

                index: undefined, // 当前查看报名表的编号
                total: undefined, // 报名表总数
                query: undefined, // 查询条件

                loading: undefined, // 全屏加载中

                andLoadingResumeNoTips: false, // 下载报名表时，是否不要提示用户下载简历
                onlyApplyTable: false, // 不显示下载报名表提示对话框时，是否仅下载报名表
                loadApplyTableDialogVisible: false, // 是否显示下载报名表对话框

                resumeId: undefined, // 要查看的简历id
                resumeDetail: undefined, // 要查看的简历信息
                resumeDialogVisible: false, // 简历对话框
                andLoadingApplyTableNoTips: false, // 下载简历时，是否不要提示用户下载报名表
                onlyResume: false, // 不显示下载简历提示对话框时，是否仅下载简历
                loadResumeDialogVisible: false, // 是否显示下载简历对话框

                noticeDialogVisible: false, // 通知消息对话框
            }
        },
        mounted() {
            this.loading = this.$loading({
                lock: true,
                text: 'Loading',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            this.initData();
        },
        methods: {
            initData() {
                this.index = this.data.number;
                this.query = this.data.query;
                this.query.page = this.index;
                this.query.limit = 1;

                // 下载报名表哦那个到的数据
                let object = this.$storage.getData('andLoadingResumeNoTips:' + this.query.activityId);
                this.andLoadingResumeNoTips = object.tips || false;
                this.onlyApplyTable = object.onlyApplyTable || false;

                // 下载简历用到的信息
                let object2 = this.$storage.getData('andLoadingApplyTableNoTips:' + this.query.activityId);
                this.andLoadingApplyTableNoTips = object2.tips || false;
                this.onlyResume = object2.onlyResume || false;

                this.getData();
            },

            // 关闭对话框
            onDialogClose() {
                this.$set(this, "dialogVisible", false);
            },

            // 查看上一个
            onLeft() {
                if (this.index > 1) {
                    this.query.page = --this.index;
                    this.getData();
                }
            },

            // 查看下一个
            onRight() {
                if (this.index < this.total) {
                    this.query.page = ++this.index;
                    this.getData();
                }
            },

            // 加载简历数据
            getData() {
                this.$axios.post("/registration/list", this.query).then(response => {
                    this.total = response.data.total;
                    if (response.data.list.length > 0) {
                        return this.$axios.get(`/registration/${response.data.list[0].id}`);
                    } else {
                        return new Promise((resolve, reject) => {
                            reject();
                        })
                    }
                }).then(response => {
                    response.data.questionnaireList.forEach(question => {
                        this.handlerQuestion(question);
                    })
                    this.applyInfo = response.data;
                    this.loading.close();
                }).catch(() => {
                    console.log("数据异常");
                });
            },

            // 点击通过报名（待处理中）
            onResolve1() {
                if (this.activity.sendNoticeConfirm === '1') {
                    this.noticeDialogVisible = true;
                } else {
                    this.applyPass();
                }
            },

            // 通过报名
            applyPass(param) {
                this.$axios.request({
                    method: "patch",
                    url: "/registration/pass",
                    params: {id: this.applyInfo.id, notifyMsg: param && param.message}
                }).then(() => {
                    if (param && param.notNotice) {
                        this.$emit('update:activity', {...this.activity, sendNoticeConfirm: '0'})
                    }
                    this.noticeDialogVisible = false;
                    this.applyInfo.status = 2;
                })
            },

            // 点击不合适（待处理中）
            onReject1() {
                this.applyReject();

                // TODO 不通过时没有确认弹窗
                // this.selectItem = item;
                // if (this.activity.sendNoticeConfirm==='1') {
                //     this.noticeDialogVisible = true;
                // } else {
                //     this.applyReject();
                // }
            },

            // 拒绝报名
            applyReject(param) {
                this.$axios.request({
                    method: "patch",
                    url: "/registration/inappropriate",
                    params: {id: this.applyInfo.id, notifyMsg: param && param.message}
                }).then(() => {
                    this.noticeDialogVisible = false;
                    this.applyInfo.status = 3;
                })
            },

            // 点击通过报名（不合适中）
            onResolve3() {
                this.$axios.patch(`/registration/pass/${this.applyInfo.id}`).then(() => {
                    this.applyInfo.status = 2;
                })
            },

            // 点击不合适（已通过中）
            onReject2() {
                this.$axios.patch(`/registration/inappropriate/${this.applyInfo.id}`).then(() => {
                    this.applyInfo.status = 3;
                })
            },

            // 查看简历
            onViewResume() {
                this.resumeId = this.applyInfo.resumeId;
                getResumeDetail(this.applyInfo.resumeId).then((response) => {
                        this.resumeDetail = response.data;
                        this.applyTableId = this.applyInfo.id;
                        this.resumeDialogVisible = true;
                    }
                );
            },

            // 点击下载简历
            onLoadResume() {
                if (this.andLoadingApplyTableNoTips) {
                    if (this.onlyResume) {
                        this.loadResume();
                    } else {
                        this.loadResume();
                        this.loadApplyTable();
                    }
                } else {
                    this.loadResumeDialogVisible = true;
                }
            },

            // 下载简历对话框中点击 否
            onLoadResumeOnly() {
                this.$storage.setData('andLoadingApplyTableNoTips:' + this.activity.id, {tips: this.andLoadingApplyTableNoTips, onlyResume: true});
                this.onlyResume = true;
                this.loadResumeDialogVisible = false;
                this.loadResume();
            },

            // 下载简历对话框中点击 是
            onLoadResumeAndResume() {
                this.$storage.setData('andLoadingApplyTableNoTips:' + this.activity.id, {tips: this.andLoadingApplyTableNoTips, onlyResume: false});
                this.onlyResume = false;
                this.loadResumeDialogVisible = false;
                this.loadApplyTable();
                this.loadResume();
            },

            // 点击下载报名表
            onLoadApplyTable() {
                if (this.applyInfo.resumeId) {
                    if (this.andLoadingResumeNoTips) {
                        if (this.onlyApplyTable) {
                            this.loadApplyTable();
                        } else {
                            this.loadApplyTable();
                            this.loadResume();
                        }
                    } else {
                        this.loadApplyTableDialogVisible = true;
                    }
                } else {
                    this.loadApplyTable();
                }
            },

            // 下载报名表对话框中点击 否
            onLoadApplyTableOnly() {
                this.$storage.setData('andLoadingResumeNoTips:' + this.query.activityId, {tips: this.andLoadingResumeNoTips, onlyApplyTable: true});
                this.onlyApplyTable = true;
                this.loadApplyTableDialogVisible = false;
                this.loadApplyTable();
            },

            // 下载报名表对话框中点击 是
            onLoadApplyTableAndResume() {
                this.$storage.setData('andLoadingResumeNoTips:' + this.query.activityId, {tips: this.andLoadingResumeNoTips, onlyApplyTable: false});
                this.onlyApplyTable = false;
                this.loadApplyTableDialogVisible = false;
                this.loadApplyTable();
                this.loadResume();
            },

            // 下载报名表
            loadApplyTable() {
                console.log("直接下载报名表");
                this.$axios.get("/export/registration-to-pdf", {params: {registrationId: this.applyInfo.id}}).then(response => {
                    if (response.data) {
                        downloadFile({
                            fileKey: response.data, fileName: '报名表.pdf', success: () => {
                            }
                        });
                    }
                })
            },

            // 下载简历
            loadResume() {
                this.$axios.get("/export/resume-to-pdf", {params: {resumeId: this.applyInfo.resumeId}}).then(response => {
                    downloadFile({
                        fileKey: response.data, fileName: '简历.pdf', success: () => {
                        }
                    });
                })
            },

            // 处理问题答案
            handlerQuestion(question) {
                switch (question.type) {
                    case "1":
                        question.answer = question.answerContent.length > 0 && question.answerContent[0];
                        break;
                    case "2":
                    case "3":
                        question.questionnaireOptions.forEach(option => {
                            option.checked = question.mustAnswer === '1' ? question.answerOptions.findIndex(answer => answer.id === option.id) >= 0 : false;
                        })
                        break;
                    case "4":
                        question.annexUrl = question.answerContent.length > 0 && question.answerContent[0];
                        break;
                }
            },

            // 判断地址是否为图片
            isPicture(url) {
                return /\.(gif|jpg|jpeg|png|GIF|JPEG|JPG|PNG)$/.test(url);
            }
        }
    }
</script>

<style scoped lang="scss">
    .dialog {
        ::v-deep .el-dialog {
            border-radius: 13px;
            padding: 20px 17px;
        }

        /deep/ .el-dialog__header {
            padding: 0;
        }

        .dialog-title {
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-bottom: 1px solid #CFD8DC;
            padding-bottom: 15px;

            .title-left {
                display: flex;
                align-items: center;

                .left-icon {
                    width: 26px;
                    height: 26px;
                    cursor: pointer;
                }

                .right-icon {
                    width: 26px;
                    height: 26px;
                    margin-left: 17px;
                    cursor: pointer;
                }

                .button {
                    width: 96px;
                    height: 28px;
                    border-radius: 4px;
                    font-size: 14px;
                    line-height: 28px;
                    text-align: center;
                    cursor: pointer;
                    margin-left: 14px;
                }

                .button1-1 {
                    background: #4895EF;
                    color: #FFFFFF;
                }

                .button1-2 {
                    background: #E3F0FF;
                    border: 1px solid #4895EF;
                    color: #4895EF;
                }

                .button2-1 {
                    background: #81C784;
                    color: #FFFFFF;
                }

                .button2-2 {
                    background: #ECEFF1;
                    color: #78909C;
                }

                .button3-1 {
                    background: #ECEFF1;
                    color: #78909C;
                }

                .button3-2 {
                    background: #EC5454;
                    color: #FFFFFF;
                }
            }

            .title-right {
                display: flex;
                align-items: center;

                .preview-icon {
                    width: 25px;
                    height: 29px;
                    cursor: pointer;
                }

                .download-icon {
                    width: 29px;
                    height: 23px;
                    margin-left: 14px;
                    cursor: pointer;
                }

                .close-icon {
                    width: 29px;
                    height: 29px;
                    margin-left: 14px;
                    cursor: pointer;
                }
            }
        }

        /deep/ .el-dialog__body {
            padding: 0;
        }

        .content-container {
            width: 100%;
            padding: 12px 47px 35px;

            .title-container {
                display: flex;
                align-items: center;
                justify-content: center;
                position: relative;

                .number {
                    position: absolute;
                    left: 0;
                    transform: translateY(-50%);
                    top: 50%;
                    font-size: 14px;
                    color: #333333;
                }

                .title {
                    font-size: 21px;
                    color: #333333;
                }
            }

            .form-container {

                .item {
                    display: flex;
                    align-items: center;
                    margin-top: 10px;

                    .required {
                        color: red;
                        width: 12px;
                    }

                    .name {
                        font-size: 16px;
                        font-weight: 400;
                        color: #666666;
                        line-height: 22px;
                    }

                    .value {
                        font-size: 16px;
                        font-weight: 400;
                        color: #333333;
                        line-height: 22px;
                    }
                }

                .question-container {

                    .question1-container {
                        margin-top: 18px;

                        .question1-line1 {
                            display: flex;
                            align-items: flex-start;
                            justify-content: space-between;

                            .title {
                                font-size: 18px;
                                color: #666666;
                                line-height: 25px;

                                .must {
                                    color: red;
                                    margin-right: 5px;
                                }
                            }
                        }

                        .question1-line2 {
                            margin-top: 5px;
                            margin-left: 12px;

                            .answer {
                                font-size: 18px;
                                color: #333333;
                                line-height: 25px;
                            }
                        }
                    }

                    .question2-container {
                        margin-top: 18px;

                        .question2-line1 {
                            display: flex;
                            align-items: flex-start;
                            justify-content: space-between;

                            .title {
                                font-size: 18px;
                                color: #666666;
                                line-height: 25px;

                                .must {
                                    color: red;
                                    margin-right: 5px;
                                }
                            }
                        }

                        .question2-line2 {
                            display: flex;
                            flex-wrap: wrap;
                            margin-top: -4px;
                            width: calc(100% + 70px);

                            .option-container {
                                display: flex;
                                align-items: flex-start;
                                margin: 4px 70px 4px 0;

                                .icon {
                                    display: flex;
                                    align-items: center;
                                    height: 25px;
                                    flex-shrink: 0;
                                }

                                .options {
                                    margin-left: 10px;
                                    font-size: 18px;
                                    font-weight: 500;
                                    color: #666666;
                                    line-height: 25px;
                                }
                            }
                        }
                    }

                    .question3-container {
                        margin-top: 18px;

                        .question3-line1 {
                            display: flex;
                            align-items: flex-start;
                            justify-content: space-between;

                            .title {
                                font-size: 18px;
                                color: #666666;
                                line-height: 25px;

                                .must {
                                    color: red;
                                    margin-right: 5px;
                                }
                            }
                        }

                        .question3-line2 {
                            display: flex;
                            flex-wrap: wrap;
                            margin-top: -4px;
                            width: calc(100% + 70px);

                            .option-container {
                                display: flex;
                                align-items: flex-start;
                                margin: 4px 70px 4px 0;

                                .icon {
                                    display: flex;
                                    align-items: center;
                                    height: 25px;
                                    flex-shrink: 0;
                                }

                                .options {
                                    margin-left: 10px;
                                    font-size: 18px;
                                    font-weight: 500;
                                    color: #666666;
                                    line-height: 25px;
                                }
                            }
                        }
                    }

                    .question4-container {
                        margin-top: 18px;

                        .question4-line1 {
                            display: flex;
                            align-items: flex-start;
                            justify-content: space-between;

                            .title {
                                font-size: 18px;
                                color: #666666;
                                line-height: 25px;

                                .must {
                                    color: red;
                                    margin-right: 5px;
                                }
                            }
                        }

                        .question4-line2 {
                            margin-top: 4px;
                            margin-left: 12px;

                            .image {
                                width: 200px;
                                display: block;
                            }

                            .link {
                                font-size: 18px;

                                ::v-deep .el-link--inner {
                                    white-space: normal;
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    .cancel-dialog {
        ::v-deep .el-dialog {
            border-radius: 13px;
        }

        /deep/ .el-dialog__body {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .button-container {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-top: 26px;

            .cancel {
                width: 107px;
                height: 35px;
                background: #FFFFFF;
                border-radius: 18px;
                border: 1px solid #4895EF;
                font-size: 16px;
                color: #4895EF;
                line-height: 35px;
                text-align: center;
                cursor: pointer;
            }

            .confirm {
                margin-left: 21px;
                width: 107px;
                height: 35px;
                background: #4895EF;
                border-radius: 18px;
                border: 1px solid #FFFFFF;
                font-size: 16px;
                color: #FFFFFF;
                line-height: 35px;
                text-align: center;
                cursor: pointer;
            }
        }
    }

    .load-resume-dialog {
        .dialog-title {
            display: flex;
            align-items: center;
            justify-content: flex-end;

            .download-icon {
                width: 29px;
                height: 23px;
                margin-left: 14px;
                cursor: pointer;
            }

            .close-icon {
                width: 29px;
                height: 29px;
                margin-left: 14px;
                cursor: pointer;
            }
        }
    }

    .resume-cancel-dialog {
        ::v-deep .el-dialog {
            border-radius: 13px;
        }

        /deep/ .el-dialog__body {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .resume-button-container {
            display: flex;
            align-items: center;
            justify-content: center;
            margin-top: 26px;

            .cancel {
                width: 107px;
                height: 35px;
                background: #FFFFFF;
                border-radius: 18px;
                border: 1px solid #4895EF;
                font-size: 16px;
                color: #4895EF;
                line-height: 35px;
                text-align: center;
                cursor: pointer;
            }

            .confirm {
                margin-left: 21px;
                width: 107px;
                height: 35px;
                background: #4895EF;
                border-radius: 18px;
                border: 1px solid #FFFFFF;
                font-size: 16px;
                color: #FFFFFF;
                line-height: 35px;
                text-align: center;
                cursor: pointer;
            }
        }
    }
</style>
