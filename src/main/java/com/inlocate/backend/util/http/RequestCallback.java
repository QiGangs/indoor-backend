package com.inlocate.backend.util.http;

import java.io.InputStream;

public interface RequestCallback {
	public abstract boolean processResult(InputStream stream);
}
