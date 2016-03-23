package com.github.callanna.iflylibaray.data;

/**
 * Created by Callanna on 2016/1/12.
 */
public class ResultCommands {
    private String command;// speed,lighting,power
    private String value;

    public ResultCommands() {
        super();
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String function) {
        this.command = command;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public void setCmdAndValueByAction(String action) {
        switch (action) {
            case "开":
            case "打开":
                this.command = "power";
                this.value = "1";
                break;
            case "关":
            case "关闭":
                this.command = "power";
                this.value = "0";
                break;
        }
    }

    public void setCmdAndValueByAction_BerifFuction(String action, String beriffuc) {
        switch (action + beriffuc) {

        }
    }

    public void setCmdAndValueByFunction(String function) {
        switch (function) {
            case "上":
                this.command = "top";
                this.value = "1";
                break;
            case "下":
                this.command = "bottom";
                this.value = "1";
                break;
            case "左":
                this.command = "left";
                this.value = "1";
                break;
            case "右":
                this.command = "right";
                this.value = "1";
                break;
            case "确定":
                this.command = "OK";
                this.value = "1";
                break;
            case "静音":
                this.command = "silence";
                this.value = "1";
                break;
            case "打开声音":
                this.command = "ring";
                this.value = "1";
                break;
            case "加声音":
                this.command = "soundup";
                this.value = "1";
                break;
            case "减声音":
                this.command = "sounddown";
                this.value = "1";
                break;
            case "频道加":
                this.command = "preprogram";
                this.value = "1";
                break;
            case "频道减":
                this.command = "nextprogram";
                this.value = "1";
                break;
            case "退出":
            case "返回":
                this.command = "back";
                this.value = "1";
                break;
            case "开机":
                this.command = "power";
                this.value = "1";
                break;
            case "关机":
                this.command = "power";
                this.value = "0";
                break;
            case "启动识别":
                this.command="reconginzer";
                this.value = "1";
                break;
            case "停止识别":
                this.command="unreconginzer";
                this.value = "0";
                break;
        }

    }

}
