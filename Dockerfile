
# 1. 기본 OpenJDK 이미지 사용
FROM openjdk:17-jdk-alpine

# 2. 작업 디렉터리 설정
WORKDIR /core/src/app

# 3. 애플리케이션 JAR 복사
COPY build/libs/CommerceCore-0.0.1-SNAPSHOT.jar CommerceCore.jar

# 4. Scouter Agent JAR 파일 복사 (로컬에서 준비한 파일 복사)
COPY scouter.agent.jar /core/src/app/scouter.agent.jar

# 5. Scouter 설정 파일 복사
COPY scouter.conf /core/src/app/conf/scouter.conf

# 6. Scouter Agent를 로드하고 애플리케이션 실행
ENTRYPOINT ["java", "-javaagent:/core/src/app/scouter.agent.jar", "-Dscouter.config=/core/src/app/conf/scouter.conf", "-jar", "CommerceCore.jar"]

# 7. 포트 노출
EXPOSE 8090

