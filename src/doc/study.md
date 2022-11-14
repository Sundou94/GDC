#VM과 PM
## Virtual Machine, Physical Machine

VM 과 intance

1. 지역 
 - 실제 데이터 서버 장소
2. cpu 
3. Memory
 - 당연히 많을수록 비싸다.
4. 부팅 디스크 설정
 - centOS ?
5. 방화벽 설정
 - https : 434번 포트 ?
 - http : 80번 포트 ?
 
ssh 와 같은 shall 로 접속이 가능하다.

--------------------------------------------------------
#DocKer 
###Containerization Application
VM 과 다르게 instance 가 프로세스 처럼 작동하기 때문에 리소스를 적게 먹는다

###IaC Infrastructure
DocKer file 운영 체제 실행 명령어를 저장 할 수 있어서 인프라를 쉽게 설정 가능하다.

###DocKer Desktop
DocKer + GUI

### 왜 DocKer 를 사용하는가?
리소스를 효율적으로 사용하고 안정적인 어플리케이션을 운영하기 위해

----------------------------------------------------------------

#GCP 에 DocKer 설치
gcp 에서 shell 실행 후 다음 명령어로 도커 설치 

    [tndus9040@instance-1 ~]$ sudo yum install docker

도커 실행

    [tndus9040@instance-1 ~]$ sudo docker run -d -p 80:80 docker/getting-started

* 참고 : 1024번 Port 이하는 관리자 권한이 필요하다. ( sudo% )

![스크린샷](/Users/dousun/Desktop/스크린샷 2022-09-25 오후 11.05.19.png)

--------------------------------------------------------
#Git hub 로 applicable 배포하기
### server 스트레스 테스트 하기 

1.인스턴트 생성 후 ssh 로 접속

2.yum 으로 wget, java 설치

    sudo yum install wget
    sudo yum install java

관리자 권한으로 wget 설치

###wget? 
GNU Wget(간단히 Wget, 이전 이름: Geturl)는 웹 서버로부터 콘텐츠를 가져오는 컴퓨터 프로그램으로, GNU 프로젝트의 일부이다. 
이 프로그램의 이름은 월드 와이드 웹과 get에서 가져온 것이다. 
HTTP, HTTPS, FTP 프로토콜을 통해 내려받기를 지원한다.

3.wget 으로 git hub에 jar file 다운로드

    wget (jar file 주소)

4. java 로 jar file 실행 


--------------------------------------------------------
#Dockerized Application GCP에 배포 하기
기본적인 베포 메커니즘은 다음과 같습니다.<p>
도커 파일 -> 도커 이미지 -> 도커 컨테이너

1. (in Local)Docker file 을 build 하면 docker image 가 생성 됩니다.
2. Docker hub 에 push 합니다.
3. GCP instance 에 접속해서 Docker hub 에서 pull 합니다.
4. 받은 docker image 를 run.

### Docker 를 사용한 Spring boot 환경 만들기 
해당 링크 참조

https://spring.io/guides/gs/spring-boot-docker/

jar 파일 생성 (gradle 기준)

    ./gradlew build && java -jar build/libs/gs-spring-boot-docker-0.1.0.jar

먼저 도커 파일을 생성해 줍니다.
    
    FROM openjdk:8-jdk-alpine
    ARG JAR_FILE=target/*.jar
    COPY ${JAR_FILE} app.jar
    ENTRYPOINT ["java","-jar","/app.jar"]

그리고 해당 명령어로 도커파일을 빌드해서 Docker image 를 생성합니다

    docker build --build-arg JAR_FILE=build/libs/\*.jar -t springio/gs-spring-boot-docker .

도커 login 

    docker login 

도커 이미지 확인 

    $ docker images

커밋

    docker commit -m [image 설명] -a [ 사용자 mail ] [ container Name or ID ] [ 생성할 image name : Tag ]

도커 실행

    $ sudo docker run -it -p {도커가 실행될 포트}:{도커 내 파일이 실행되는 포트} {이미지 이름}:{태그 이름}

도커 컨테이너 확인

    $ docker ps

도커 실행 취소

    $ docker stop {컨테이너 아이디}

---------------------------------------------

#Jenkins 를 이용해서 배포하기

##Jenkins 를 GCP instance 에 설치

jenkins 를 설치한 instance 와 실제 서비스 로직이 들어있는 instance 를
구분해줍니다. 

각각 jenkins instance 와 worker instance 라고 네이밍 합니다.

## jenkins 인스턴스에서 실행하는 명령어 
    
    sudo yum install wget
    sudo yum install maven
    sudo yum install git
    sudo yum install docker
    
    sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
    sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
    sudo yum install jenkins
    sudo systemctl start jenkins
    sudo systemctl status jenkins

    sudo cat /var/lib/jenkins/secrets/initialAdminPassword

---

# (다시 정리) docker 프로젝트 올리기

1. jar file 만든다 Gradle build boot.jar 사용
2. build/libs/hynixMenu-0.0.1-SNAPSHOT.jar file 생성
3. jar file 기준으로 docker image 생성
4. docker hub 에 업로드
5. instance 에서 docker image file pull (그 전에 docker 실행 해줘야한다....ㅠ)
 
###도커 실행 방법

    $sudo systemctl start docker
    $sudo systemctl enable docker


###으아아아아 M1 mac 에서 빌드 했을경우 나는 오류 ㅋㅋㅋ 

이 오류는 ARM 기반 Apple M1 Pro 칩이 탑재 된 MacBook Pro 에서 이미지를 빌드한 경우에도 발생할 수 있으므로 기본적으로 Docker 빌드 명령은 .arm64

Docker는 실제로 Apple M1 Pro 플랫폼을 다음과 같이 감지합니다.linux/arm64/v8

빌드 명령과 버전 태그 모두에 플랫폼을 지정하면 충분합니다.

    # Build for ARM64 (default)
    docker build --platform linux/amd64 -t douseon/hynix-menu .


### 그리고 jar file 잘 만들어 주자.... gradle build 로 했는데 안되더라..  Unable to access jarfile /app.jar 에러 뜨더라 ㅡㅡ

    docker build --build-arg JAR_FILE=build/libs/\*.jar --platform linux/amd64 -t douseon/hynx-menu .


### 그리고 마지막으로 jenkins Exec command 에 

    nohup sudo docker run -p 8080:80 douseon/hynix-menu:latest > /dev/null2>&1 > &

>nohup & 은 background 로 실행한다는 뜻! 

---

## bash: /dev/null: Permission denied 에러 해결법
윈도우에 휴지통을 다시 지우고 생성하는 느낌이란다.. 뭔지는 정확하게 모르겠음

    # rm -rf /dev/null
    # mknod -m 0666 /dev/null c 1 3
    그리고 또 이런 오류가 나면 666 으로 권한 주자 

다시한번 jenkins 에서 build 하니까 permission denied 는 안난다..
build 가 계속 성공해서 원인을 몰랐는데 다음 부터는 콘솔을 잘 확인하자.. 


## 화가난다..  /dev/null 2>&1  띄어쓰기를 잘보자.. 

아래 명령어는 표준 출력을 /dev/null로 redirection 하라는 의미로 표준출력을 버리라는 의미입니다.

    $ rm test > /dev/null

하지만, test라는 file이 존재하지 않는 경우 아래와 같은 표준에러가 출력됩니다.

    $ rm test > /dev/null
    rm: cannot remove 'test': No such file or directory

방금 언급했지만, 이것은 "표준출력"이 아닌 "표준에러"이기 때문에 화면에 출력이 된것입니다.
다시 강조하면 위에 예제는 /dev/null 로 redirection 을 통해 화면 출력을 하지 않는것은 표준출력에 한해서 해당됩니다.


자 그렇다면, 표준에러를 버리고 싶다면 아래와 같이 사용하도록 합시다,

    $ rm test > /dev/null 2>&1

2>&1 는 표준에러를 표준출력으로 redirection 하라는 의미입니다.

    0 : 표준입력
    1 : 표준출력
    2 : 표준에러

정리하면 "rm test"의 표준출력을 /dev/null로 버리는데, 표준에러는 표준출력으로 redirection 합니다.
결국 결과는 표준출력이 되기 때문에 /dev/null로 버려지고, 화면에 결과가 뿌려지지 않게 되는 것입니다.


---

#무중단 배포 환경?

당연하게도 배포를 중단 없이 한다는 말

배포는 새로 만든 걸 패키징 하여
도커 컨터이너로 app 을 실행 시킬때 old 버전은 중단되므로
서비스가 잠깐 중단된다.

어플리케이션 서버를 2대로 하면 서비스가 종료 되지 않지 않을까?

근데 그럼 사용자들을 2개의 서버의 ip를 모두 가지고 있어야한다.
그래서 중간에 리버스 프록시를 사용한다

프록시는 일반적으로 클라이언트를 숨겨주는 역할을 하는데
리버스 프록시는 서버를 숨기고 클라이언트에게 보여준다
오.. 그리고 트레픽을 분산하는 기능도 가능하다

현재시점에 리버스 프록시와 로드벨런싱을 하는 가장 인기 있는 서버는 그 유명한
Nginx 이다.


---

#Nginx를 통한 로드 밸런싱 구성

1. 기존에 만들었던 인스턴스를 복사해서 3개를 만든다
    - GCP의 머신이미지 메뉴를 이용하면 쉽게 인스턴스를 복사 할 수 있다.
2. jenkins의 빌드파일에 배포 스크립트를 추가해준다
   - 'nohup.out' 로그 표시를 위해 추가
3. yum install nginx 를 통해 nginx 를 설치해준다
4. nginx 를 실행 해준다
5. 로드 벨런싱을 위해 /etc/nginx/nginx.conf 파일을 수정해준다

---

#sourcetree
