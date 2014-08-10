package pl.panryba.mc.broadcast;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class BroadcasterTest {
    private Broadcaster bc;
    private TestOutput output;
            
    private class TestOutput implements BroadcastOutput {
        private String lastOutput;
        
        public String getLastOutput() {
            return this.lastOutput;
        }
        
        @Override
        public void broadcast(String message) {
            this.lastOutput = message;
        }
        
    }
    
    @Before
    public void setUp() {
        this.output = new TestOutput();
        this.bc = new Broadcaster(this.output);
    }
    
    @Test
    public void testAdd() {
        bc.addMessage("Test message");        
        assertTrue(bc.getMessages().contains("Test message"));
    }
    
    @Test
    public void testAddingEmptyMessage() {
        bc.addMessage("");
        assertEquals(0, bc.getMessages().size());
        
        bc.addMessage(null);
        assertEquals(0, bc.getMessages().size());
    }
    
    @Test
    public void testRemove() {
        bc.addMessage("Test message");
        bc.removeMessage(0);
        
        assertFalse(bc.getMessages().contains("Test message"));
    }
    
    @Test
    public void removingOOBIndex() {
        bc.addMessage("Test message");
        try {
           bc.removeMessage(1);
           assertTrue(bc.getMessages().contains("Test message"));
        }
        catch(Exception ex) {
            fail(ex.toString() + " has been thrown");
        }
    }
    
    @Test
    public void testBroadcastWhenEmpty() {
        bc.broadcast();
    }
    
    @Test
    public void testBroadcast() {
        bc.addMessage("Test 1");

        bc.broadcast();
        assertEquals(this.output.getLastOutput(), "Test 1");
        bc.broadcast();
        assertEquals(this.output.getLastOutput(), "Test 1");

        bc.addMessage("Test 2");
        bc.addMessage("Test 3");
        
        bc.broadcast();
        assertEquals(this.output.getLastOutput(), "Test 2");
        bc.broadcast();
        assertEquals(this.output.getLastOutput(), "Test 3");
        bc.broadcast();
        assertEquals(this.output.getLastOutput(), "Test 1");
    }
    
    @Test
    public void testEdit() {
        bc.addMessage("Test message");
        bc.editMessage(0, "Edited message");
        
        assertFalse(bc.getMessages().contains("Test message"));
        assertTrue(bc.getMessages().contains("Edited message"));
    }
    
    @Test
    public void testOOBEdit() {
        bc.addMessage("Test message");
        bc.editMessage(1, "Edited");
        
        assertEquals(1, this.bc.getMessages().size());
        assertFalse(this.bc.getMessages().contains("Edited"));
        assertTrue(this.bc.getMessages().contains("Test message"));
    }

    @Test
    public void testTokens() {
        bc.addMessage("Test $existing$ message $nonexisting$");

        assertEquals(0, bc.getTokens().size());
        bc.setToken("existing", "Token");
        assertEquals(1, bc.getTokens().size());

        bc.broadcast();
        assertEquals(this.output.getLastOutput(), "Test Token message $nonexisting$", this.output.getLastOutput());

        assertTrue(bc.removeToken("existing"));
        assertEquals(0, bc.getTokens().size());
    }

    @Test
    public void testMultipleToken() {
        bc.addMessage("$test$ $test$");
        bc.setToken("test", "pass");
        bc.broadcast();
        assertEquals(this.output.getLastOutput(), "pass pass", this.output.getLastOutput());
    }
}

