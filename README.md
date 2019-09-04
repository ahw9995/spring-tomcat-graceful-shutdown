# spring-tomcat-graceful-shutdown
Graceful shutdown(우아한 연결종료) 를 적용한 tomcat 기반 springboot 2.x.x 버전 프로젝트

<hr/>

## 하고자 했던 것(혹은 원했던 것)
1. 서버에 여러 사용자들이 붙어서 프로세스를 처리 중 서버를 kill 했을 경우
2. 이미 threadpool 에서 처리 중인 업무들은 모두 처리시킴
3. kill 한 시점에서 들어오는 api call 들은 모두 막혀야 함
4. 하지만 톰캣 기반의 경우 **3.** 이 제대로 동작하지 않았음
5. 테스트 해본 결과로는 threadpool 안에 있는 업무들을 처리하는 동안 kill 이후 api를 call했던 사용자들은 무한대기 상태로 빠지게 되며,
6. threadpool 안에 모든 업무가 처리되고 shutdown이 되는 순간 kill 이후 api를 call했던 사용자들도 오류를 받게 됨
7. proxy 등 서버 상태를 확인해서 분기를 태워줄 경우 위와 같이 처리 시 문제가 생김
8. 그래서 **protocolHandlerClose** 메소드를 만들어 직접 protocolHandler를 건들임
9. 원하는대로 동작은 하나, 아래 **주의사항** 처럼 side effect에 대한 부담감이 있어 실제 운영서버에 적용하는 것을 고민중

<pre><code>
private void protocolHandlerClose() {
    try {
      Class<?> clazz = Class.forName(protocolHandlerClassName);
      ProtocolHandler p = (ProtocolHandler) clazz.getConstructor().newInstance();
      p.pause();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
</code></pre>

9. 기본적으로 구글링에서 볼 수 있는 코드들은 connector의 **pause()** 를 사용함
<pre><code>
  // this.protocolHandlerClose();
  this.connector.pause();
</code></pre>

## 주의사항
* protocol handler를 위와 같이 컨트롤 하는 것에 대해 부담감이 있음(side effect)
* Stack overflow나 위 문제로 고민했던 github thread에 글을 올려 확인 중이며, 다른 개발자들의 의견을 듣고 싶음

<hr/>

## 개발환경
Springboot 2.1.7 RELEASE
maven
