import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.junit5.JUnit5Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class WebClientTestJMock {

	@RegisterExtension
	Mockery context = new JUnit5Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	@Test
	public void testGetContentOk() throws Exception {
		ConnectionFactory factory = context.mock(ConnectionFactory.class);
		InputStream mockStream = context.mock(InputStream.class);

		context.checking(new Expectations() {
			{
				oneOf(factory).getData();
				will(returnValue(mockStream));

				atLeast(1).of(mockStream).read();
				will(onConsecutiveCalls(returnValue((int) 'W'),
						returnValue((int) 'o'), returnValue((int) 'r'),
						returnValue((int) 'k'), returnValue((int) 's'),
						returnValue((int) '!'), returnValue(-1)));

				oneOf(mockStream).close();
			}
		});

		WebClient client = new WebClient();
		String workingContent = client.getContent(factory);

		assertEquals("Works!", workingContent);
	}

	@Test
	public void testGetContentCannotCloseInputStream() throws Exception {
		ConnectionFactory factory = context.mock(ConnectionFactory.class);
		InputStream mockStream = context.mock(InputStream.class);

		context.checking(new Expectations() {
			{
				oneOf(factory).getData();
				will(returnValue(mockStream));
				oneOf(mockStream).read();
				will(returnValue(-1));
				oneOf(mockStream).close();
				will(throwException(new IOException("cannot close")));
			}
		});
		
		WebClient client = new WebClient();
		String workingContent = client.getContent(factory);
		assertNull(workingContent);
	}
}
