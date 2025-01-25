package org.jbehavesupport.test.sample;

import org.jbehavesupport.core.AbstractSpringStories;

import java.util.Arrays;
import java.util.List;

public class VoteForLiliIT extends AbstractSpringStories {

    @Override
    protected List<String> storyPaths() {
        return Arrays.asList(
            "org/jbehavesupport/test/sample/VOteForLili.story"
        );
    }
}
