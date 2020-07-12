package dev.xframe.admin.system.auth;

import io.netty.handler.codec.http.HttpMethod;

public class Unblocked {
	
	final String path;
	final HttpMethod method;
	
	Unblocked(String path, HttpMethod method) {
		this.path = path;
		this.method = method;
	}
	public boolean match(HttpMethod method) {
		return this.method == null || this.method.equals(method) || HttpMethod.OPTIONS.equals(method);
	}
	public static Unblocked of(String path) {
		return of(path, null);
	}
	public static Unblocked of(String path, HttpMethod method) {
		return new Unblocked(path, method);
	}

}
