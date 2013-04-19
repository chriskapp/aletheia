package com.k42b3.aletheia.processor;

import java.util.Properties;

public interface ProcessPropertiesCallback
{
	public void onSubmit(Properties properties);
	public void onCancel();
}
