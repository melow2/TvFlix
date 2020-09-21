package com.khs.androidbaseproject.network

import okhttp3.Call
import okhttp3.Request
import retrofit2.Retrofit


/**
 * 1) internal은 같은 모듈 내에서만 바라볼 수 있음.
 * 2) inline:
 * 고차함수(함수를 인자로 전달하거나 함수를 리턴)를 사용할 때, 추가적인 메모리 할당 및 메소드 호출로 런타임 오버헤드가 발생할 수 있다.
 * 컴파일 단계에서 호출 방식이 아닌 코드 자체가 복사되는 방식으로 변환, 함수 앞에 inline 키워드를 붙여 사용.
 * 람다 전달 시 발생하는 메모리, 호출등의 오버헤드를 감소시키기 위해 인라인 함수를 사용.
 * 원칙적으로 inline 함수 내에서 전달받은 함수는 다른 함수로 전달되거나 참조 될 수 없다.
 * 따라서 참조하려면 noinline 키워드를 삽입해야 한다.
 * @author 권혁신
 * @version 1.0.0
 * @since 2020-09-21 오후 12:29
 **/

@PublishedApi
internal inline fun Retrofit.Builder.callFactory(
    crossinline body: (Request) -> Call
) = callFactory(object : Call.Factory {
    override fun newCall(request: Request): Call = body(request)
})