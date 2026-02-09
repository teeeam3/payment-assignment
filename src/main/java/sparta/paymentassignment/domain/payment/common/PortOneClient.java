package sparta.paymentassignment.domain.payment.common;

import sparta.paymentassignment.domain.payment.dto.PortOneResponse;

public interface PortOneClient {

    //포트원 서버로부터 결제 상세 정보를 조회
    PortOneResponse verify(String impUid);


    //결제 오류 시 포트원 API를 통해 결제 취소를 요청
    void cancel(String impUid, String reason);
}
