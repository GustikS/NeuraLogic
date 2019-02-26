/*
 * Copyright (c) 2015 Ondrej Kuzelka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ida.utils;

import ida.utils.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for easy parallelization providing several useful methods for dividing work between
 * several threads and then waiting until they finish their jobs.
 * 
 * @author admin
 */
public class Parallel {

    private boolean stop = false;

    private final List<WorkerThread> workers = Collections.synchronizedList(new ArrayList<WorkerThread>());

    private final List<Pair<Runnable,Int>> tasks = Collections.synchronizedList(new ArrayList<Pair<Runnable,Int>>());

    /**
     * Creates a new instance of class Parallel with specified number of threads.
     * @param threadCount the number of threads to be used
     */
    public Parallel(int threadCount){
        for (int i = 0; i < threadCount; i++){
            WorkerThread worker = new WorkerThread();
            workers.add(worker);
            worker.start();
        }
    }

    /**
     * Stops the threads (can take a while because all threads must finish their
     * "atomic" tasks, therefore it is a good idea to have short "atomic takss") 
     */
    public void stop(){
        this.stop = true;
        tasks.notifyAll();
    }

    /**
     * Runs the tasks given as Runnables iterable parallel and waits until they are finished.
     * 
     * @param tasks the tasks which should be performed
     */
    public void runTasks(List<? extends Runnable> tasks){
        Runnable[] ts = new Runnable[tasks.size()];
        int i = 0;
        for (Runnable task : tasks){
            ts[i] = task;
            i++;
        }
        runTasks(ts);
    }

    /**
     * Runs the tasks given as Runnables iterable parallel and waits until they are finished.
     * 
     * @param tasks the tasks which should be performed
     */
    public void runTasks(Runnable ...tasks){
        final Int counter = new Int(tasks.length);
        for (Runnable r : tasks){
            synchronized (this.tasks){
                this.tasks.add(new Pair<Runnable,Int>(r,counter));
                this.tasks.notify();
            }
        }
        synchronized (counter){
            while (counter.value > 0){
                try {
                    counter.wait();
                } catch (InterruptedException ie){
                    ie.printStackTrace();
                }
            }
        }
    }

    private static class Int {
        
        int value;

        public Int(){}

        public Int(int value){
            this.value = value;
        }
    }

    private class WorkerThread extends Thread {

        public WorkerThread(){
            this.setDaemon(true);
        }

        @Override
        public void run(){
            while (!stop){
                Pair<Runnable,Int> task = null;
                synchronized (tasks){
                    if (tasks.isEmpty()){
                        try {
                            tasks.wait();
                        } catch (InterruptedException ie){
                            ie.printStackTrace();
                        }
                    }
                    if (tasks.size() > 0){
                        task = tasks.remove(tasks.size()-1);
                    }
                }
                if (task != null){
                    task.r.run();
                    synchronized (task.s){
                        task.s.value--;
                        task.s.notify();
                    }
                }
            }
        }
    }

    public void finalize(){
        this.stop = true;
    }
}
