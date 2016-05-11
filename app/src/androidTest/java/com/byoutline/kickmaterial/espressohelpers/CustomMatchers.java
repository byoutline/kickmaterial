package com.byoutline.kickmaterial.espressohelpers;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.test.rule.ActivityTestRule;
import android.view.View;
import android.widget.EditText;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Contains static methods returning custom espresso matchers. <br />
 * Use with import static CustomMatchers.*
 *
 * @author Sebastian Kacprzak <sebastian.kacprzak at byoutline.com>
 */
public class CustomMatchers {
    public static Matcher<View> withErrorSet(ActivityTestRule testRule, @StringRes int expected) {
        return withErrorSet(getString(testRule, expected));
    }

    public static Matcher<View> withErrorSet(@NonNull final String expected) {
        if (expected == null) {
            throw new AssertionError("Null string passed");
        }
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof EditText)) {
                    return false;
                }
                EditText editText = (EditText) view;
                CharSequence error = editText.getError();
                if (error == null) {
                    return false;
                }
                return expected.equals(error.toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("view should have error: ").appendValue(expected).appendText(" set");
            }
        };
    }

    public static String getString(ActivityTestRule testRule, @StringRes int stringId) {
        return testRule.getActivity().getString(stringId);
    }
}
