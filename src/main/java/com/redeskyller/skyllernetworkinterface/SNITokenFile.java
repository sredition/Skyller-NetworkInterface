package com.redeskyller.skyllernetworkinterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import com.j256.twofactorauth.TimeBasedOneTimePasswordUtil;

public class SNITokenFile {

	private File file;

	public SNITokenFile(final File file)
	{
		this.file = file;
	}

	public String getToken()
	{
		String token = null;
		try {

			if (!this.file.exists()) {
				this.file.getParentFile().mkdirs();

				try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(this.file))) {
					writer.write(TimeBasedOneTimePasswordUtil.generateBase32Secret());
					writer.write(System.lineSeparator());
				}
			}

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)))) {
				token = reader.readLine();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return token;
	}
}
