package gcj2014.roundBs;

/**
 * Created by hippo on 8/29/14.
 */
public class GCJTest extends GCJ {
    public GCJTest(String inName, String outName, boolean multi) throws Exception {
        super(inName, outName, multi);
    }

    public static void main(String[] args) throws Exception {
        new GCJTest("test.in", "test.out", true).run();
    }

    @Override
    public Object reader(FileWrapper input) {
        Case c = new Case();
        return c;
    }

    @Override
    public String solver(Object data) {
        Case c = (Case) data;
        return "";
    }

    static class Case {
    }
}
