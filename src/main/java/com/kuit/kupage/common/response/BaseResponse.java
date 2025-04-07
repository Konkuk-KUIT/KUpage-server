package com.kuit.kupage.common.response;


public record BaseResponse<T>(
        boolean isSuccess,
        ResponseStatus responseCode,
        String responseMessage,         // ResponseStatus에도 message 필드가 있는데 여기에도 message 필드를 둘 필요성을 잘 모르겠습니다. 더 구체적인 메시지를 클라이언트에게 제공하기 위해서인가요?
        T result) {
}
