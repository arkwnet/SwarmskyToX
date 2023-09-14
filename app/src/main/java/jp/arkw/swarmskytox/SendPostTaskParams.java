package jp.arkw.swarmskytox;

public class SendPostTaskParams {
    String url;
    String postData;

    SendPostTaskParams(String url, String postData) {
        this.url = url;
        this.postData = postData;
    }
}
