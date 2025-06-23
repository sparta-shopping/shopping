package com.example.shopping.common.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

	@Value("${spring.jasypt.encryptor.password}")
	private String password;

	@Bean(name = "jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(password); // 암호화에 사용할 비밀번호 설정
		config.setAlgorithm("PBEWithMD5AndDES"); // 사용할 암호화 알고리즘을 설정
		config.setKeyObtentionIterations("1000"); // 키 생성시 해싱 반복 횟수 설정. (높을수록 좋지만 그만큼 성는저하)
		config.setPoolSize("1"); // 스레드 풀 크기 설정 (애플리케이션 머신의 코어 수와 동일하게 설정하는것을 권장한다)
		config.setProviderName("SunJCE"); // 사용할 프로바이더 이름을 설정 (Java의 표준 암호화 제공자)
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // Salt 생성에 사용할 클래스를 지정 한다.
		config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator"); // 초기화 백터(VI) 생성에 사용할 클래스를 지정한다.
		config.setStringOutputType("base64"); // 암호화된 결관물 출력 형식 지정
		encryptor.setConfig(config); // 위에서 설정한 SimpleStringPBEConfig의 설정을 encryptor 객체에 적용
		return encryptor;
	}
}
