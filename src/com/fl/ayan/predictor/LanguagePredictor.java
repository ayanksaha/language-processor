package com.fl.ayan.predictor;

import com.fl.ayan.processor.LanguageProcessor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;

public class LanguagePredictor {

    public String detectClosestLanguage(
            final File localDirectory, final int nGram) {
        final List<File> directories = getSubDirectories(localDirectory);
        System.out.println(directories);
        final LanguageProcessor processor = new LanguageProcessor();
        final Map<String, Map<String, Integer>> listOfLanguageModels =
                processor.formNGramModelsForAllLanguages(directories, nGram);
        final Map<String, Integer> mysteryTextModel = processor.formNGramModelForMysteryText(localDirectory, nGram);
        return processor.detectClosestLanguage(listOfLanguageModels, mysteryTextModel);
    }

    private ArrayList<File> getSubDirectories(final File localDirectory) {
        return new ArrayList<>(
                Arrays.asList(
                        Objects.requireNonNull(localDirectory.listFiles(File::isDirectory))
                )
        );
    }
}
