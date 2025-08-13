package app.chrisk.aletheia.processor;

import javax.swing.JFrame;

/**
 * ProcessPropertiesAbstract
 *
 * @author Christoph Kappestein <christoph.kappestein@gmail.com>
 * @since 0.1
 */
abstract public class ProcessPropertiesAbstract extends JFrame
{
	abstract public void setCallback(ProcessPropertiesCallback callback);
}
