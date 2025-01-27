package org.hiero.sample.view;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;

@Service
@ApplicationScope
public class FrontendUpdateService {

    private List<Runnable> tasks = new CopyOnWriteArrayList<>();

    @Async
    @Scheduled(fixedRate = 500)
    public void updateAllVisibleViews() {
        tasks.forEach(task -> task.run());
    }

    public void addTask(Runnable runnable) {
        tasks.add(runnable);
    }

    public void removeTask(Runnable runnable) {
        tasks.remove(runnable);
    }
}
