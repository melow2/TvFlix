repositories {
    jcenter()
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

kotlinDslPluginOptions {
    /*
    * 기본적으로 Kotlin_DSL 플러그인은 Kotlin 컴파일러의 실험 기능을 사용하는 것에 대해 경고한다.
    * 다음과 같이 실험 경고 속성을 false로 하여 경고를 방지 할 수 있다.
    * */
    experimentalWarning.set(false)
}
