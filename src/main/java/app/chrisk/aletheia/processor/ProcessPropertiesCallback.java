package app.chrisk.aletheia.processor;

import java.util.Properties;

/**
 * ProcessPropertiesCallback
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
public interface ProcessPropertiesCallback
{
	void onSubmit(Properties properties);
	void onCancel();
}
