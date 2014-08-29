 

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by hippo on 8/29/14.
 */
public abstract class GCJ implements Runnable {
    //for 4 cup cores suits
    public static int MAX_THREAD_NUM = 4;
    protected boolean multi = false;
    private StringBuilder output = new StringBuilder();
    private FileWrapper fw;
    private PrintWriter writer;
    private Object[] data;

    public GCJ(String inName, String outName, boolean multi) throws Exception {
        this.multi = multi;
        this.fw = new FileWrapper(inName);
        this.writer = new PrintWriter(outName);
        System.out.println("GCJ successfully initlized!");
    }

    @Override
    public void run() {
        read();
        String[] answer;
        if (multi)
            answer = process(data);
        else
            answer = multiProcess(data, MAX_THREAD_NUM);
        write(answer);
    }

    public void write(String[] answer) {
        for (int i = 0; i < answer.length; i++)
            output.append("Case #" + i + 1 + ": " + answer[i] + "\n");
        writer.write(output.toString());
    }

    private void read() {
        int size = fw.getInt();
        data = new Object[size];
        for (int i = 0; i < size; i++) {
            data[i] = reader(fw);
        }
    }

    public String[] process(Object[] data) {
        long curr = System.currentTimeMillis();
        int N = data.length;
        String[] answers = new String[N];
        for (int i = 0; i < N; i++) {
            System.out.printf("%s ms: working on Case %d\n", System.currentTimeMillis(), i + 1);
            answers[i] = solver(data[i]);
            System.out.printf("%s ms: delta with Case %d, answer: %s\n", System.currentTimeMillis(), i + 1, answers[i]);
        }
        System.out.println("solved cases in " + (System.currentTimeMillis() - curr) / 1000 + "," + (System.currentTimeMillis() - curr) + "ms");
        return answers;
    }

    public String[] multiProcess(final Object[] data, int numProcess) {
        long curr = System.currentTimeMillis();
        final String[] answers = new String[data.length];
        class IdxHolder {
            int idx = 0;
        }
        final IdxHolder lock = new IdxHolder();
        final Boolean[] loads = new Boolean[data.length];
        for (int i = 0; i < numProcess; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        int ii = 0;
                        synchronized (lock) {
                            ii = lock.idx;
                            if (lock.idx == data.length) {
                                lock.notify();
                                break;
                            }
                        }
                        synchronized (loads[ii]) {
                            if (!loads[ii]) {
                                System.out.printf("%s ms: working on Case %d\n", System.currentTimeMillis(), ii + 1);
                                answers[ii] = solver(data[ii]);
                                System.out.printf("%s ms: delta with Case %d, answer: %s\n", System.currentTimeMillis(), ii + 1, answers[ii]);
                                loads[ii] = true;
                                synchronized (lock) {
                                    if (lock.idx == ii)
                                        lock.idx++;
                                    //else do nothing and process next idx search(job pointer must be moved by another thread)
                                }
                            }
                        }
                    }
                }
            }).start();
        }
        try {
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("solved cases in " + (System.currentTimeMillis() - curr) / 1000 + "," + (System.currentTimeMillis() - curr) + "ms");
        return answers;
    }

    //read a case
    public abstract Object reader(FileWrapper input);

    //we will case it for our suit
    //write a case
    public abstract String solver(Object data);

    public void finalize() {
        writer.close();
    }

    static class FileWrapper {
        private Scanner input;
        private File file;

        public FileWrapper(String path) throws Exception {
            file = new File(path);
            if (!file.exists())
                throw new Exception("file not exist!");
            input = new Scanner(file);
        }

        public int getInt() {
            return input.nextInt();
        }

        public float getFloat() {
            return input.nextFloat();
        }

        public double getDouble() {
            return input.nextDouble();
        }

        public long getLong() {
            return input.nextLong();
        }

        public String[] getWords() {
            return input.nextLine().trim().split("\\s+");
        }

        public int[] getInts() {
            String[] ints = input.nextLine().trim().split("\\s+");
            int[] res = new int[ints.length];
            for (int i = 0; i < ints.length; i++)
                res[i] = Integer.parseInt(ints[i]);
            return res;
        }

        public float[] getFloats() {
            String[] floats = input.nextLine().trim().split("\\s+");
            float[] res = new float[floats.length];
            for (int i = 0; i < floats.length; i++)
                res[i] = Float.parseFloat(floats[i]);
            return res;

        }

        public double[] getDoubles() {
            String[] doubles = input.nextLine().trim().split("\\s+");
            double[] res = new double[doubles.length];
            for (int i = 0; i < doubles.length; i++)
                res[i] = Double.parseDouble(doubles[i]);
            return res;
        }

        @Override
        public void finalize() {
            input.close();
        }
    }
}
