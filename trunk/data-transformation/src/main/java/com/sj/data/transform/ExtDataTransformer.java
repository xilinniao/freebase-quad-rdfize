package com.sj.data.transform;

public interface ExtDataTransformer <T> {
	public T transformData (final String assertion) throws Exception;
}
