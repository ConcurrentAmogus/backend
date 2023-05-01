package com.concurrent_programming.amogus;

import com.concurrent_programming.amogus.Config.FirebaseConfig;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class AmogusApplication {

	public static void main(String[] args) throws IOException {
		FirebaseConfig db = new FirebaseConfig();
		db.connect();

		SpringApplication.run(AmogusApplication.class, args);
	}

}
