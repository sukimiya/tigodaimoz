@REM setx JAVA_HOME "C:\Users\sukim\.jdks\openjdk-24.0.1"
@REM setx PATH "%PATH%;C:\Users\sukim\.jdks\openjdk-24.0.1\bin"

gradlew.bat :tigor-gateway:build -x test

gradlew.bat :tigor-eureka:build -x test

