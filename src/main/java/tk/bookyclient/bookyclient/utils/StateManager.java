package tk.bookyclient.bookyclient.utils;
// Created by booky10 in bookyClient (11:26 15.04.21)

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This is not written be me. Licensed under MIT License,
 * click on the link at the bottom for the original repository.
 * <p>
 * Allows registering objects to be reset after a crash. Objects registered
 * use WeakReferences, so they will be garbage-collected despite still being
 * registered here.
 *
 * @author https://github.com/DimensionalDevelopment/VanillaFix
 */
public class StateManager {

    public interface IResettable {

        default void register() {
            RESETTABLE_REFS.add(new WeakReference<>(this));
        }

        void resetState();
    }

    private static final Set<WeakReference<IResettable>> RESETTABLE_REFS = new HashSet<>();

    public static void resetStates() {
        Iterator<WeakReference<IResettable>> iterator = RESETTABLE_REFS.iterator();

        while (iterator.hasNext()) {
            IResettable resettable = iterator.next().get();

            if (resettable != null) {
                resettable.resetState();
            } else {
                iterator.remove();
            }
        }
    }
}