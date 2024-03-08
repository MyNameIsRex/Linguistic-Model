package data.psychologytheory.linguisticmodel;

/*
====================================================================================================

  LINGUISTIC MODEL     by Rex Huang (Feb 1, 2024 ~ Feb 5, 2024 & Feb 14, 2024)

  Documentation Here:
  https://docs.google.com/document/d/1YuSskrAupnGG1nrvtkYVVj0KVV6QGbzcYoBlzv2w1Gw/edit?usp=sharing

====================================================================================================
 */

import java.util.ArrayList;
import java.util.List;

public class LinguisticModel {
    //Character Array of Vowels, SHOULD NOT BE MODIFIED!!!
    public final char[] vowels = {'i', 'y', 'ɨ', 'ʉ', 'ɯ', 'u', 'ɪ', 'ʏ', 'ʊ', 'e', 'ø', 'ɘ', 'ɵ', 'ɤ', 'o', 'ə', 'ɛ', 'œ', 'ɜ', 'ɞ', 'ʌ', 'ɔ', 'ɐ', 'æ', 'a', 'ɶ', 'ɑ', 'ɒ'};

    public String input;
    public String output;

    public int rootLocation;

    public List<String> syllables;
    public List<Integer> vowelIndicies;
    public List<Integer> hyphenIndicies;

    public LinguisticModel() {
        this.rootLocation = 0;
        this.syllables = new ArrayList<>();
        this.vowelIndicies = new ArrayList<>();
        this.hyphenIndicies = new ArrayList<>();
    }

    public void initialize(String input) {
        this.input = input;
        this.output = "";
        this.rootLocation = 0;
        this.syllables.clear();
        this.vowelIndicies.clear();
        this.runProgram();
    }

    private void runProgram() {
        this.interpretInputWord();
        this.parseSyllables();
    }

    private void interpretInputWord() {
        this.identifyLocations();
        this.createSyllableList();
    }

    private void identifyLocations() {
        for (int index = 0; index < this.input.length(); index++) {
            if (this.input.charAt(index) == '[') {
                this.rootLocation = index + 1;
            }

            for (char character : this.vowels) {
                if (character == this.input.charAt(index)) {
                    this.vowelIndicies.add(index);
                }
            }
        }
    }

    private void createSyllableList() {
        StringBuilder syllable = new StringBuilder();
        for (int index : this.vowelIndicies) {
            if (index - 2 >= 0 && ((this.isLetter(this.input.charAt(index - 2)) && this.isNotVowel(this.input.charAt(index - 2)) && !this.isGlide(this.input.charAt(index - 2))) ||
                    (this.input.charAt(index - 2) == '-') && this.isLetter(this.input.charAt(index - 1)))) {
                syllable.append(this.input.charAt(index - 2));
            }

            if (index - 1 >= 0 && (this.isLetter(this.input.charAt(index - 1)) && this.isNotVowel(this.input.charAt(index - 1)) || this.input.charAt(index - 1) == '-')) {
                syllable.append(this.input.charAt(index - 1));
            }

            syllable.append(this.input.charAt(index));

            if (index + 1 < this.input.length() && (this.isGlide(this.input.charAt(index + 1)) || this.isLongVowelIndicator(this.input.charAt(index + 1)))) {
                syllable.append(this.input.charAt(index + 1));
            }

            this.syllables.add(syllable.toString());
            syllable.setLength(0);
        }
    }

    private void parseSyllables() {
        StringBuilder parsedWord = new StringBuilder();
        List<String> unparsedSyllables = this.syllables;
        String unparsedLightSyllable = "";
        boolean primaryStressParsed = false;

        if (this.input.contains("[")) {
            parsedWord.append(this.input, 0, this.rootLocation);

            for (String syllable : unparsedSyllables) {
                if (this.input.charAt(this.rootLocation) == syllable.charAt(0) && this.input.charAt(this.rootLocation + syllable.length() - 1) == syllable.charAt(syllable.length() - 1)) {
                    unparsedSyllables = unparsedSyllables.subList(unparsedSyllables.indexOf(syllable), unparsedSyllables.size());
                    break;
                }
            }
        }

        if (unparsedSyllables.size() == 1) {
            parsedWord.append("('").append(unparsedSyllables.get(0)).append(')');
            this.output = parsedWord.toString();
            return;
        }

        if (unparsedSyllables.size() == 2) {
            if (!this.input.contains("[") || this.isHeavySyllable(unparsedSyllables.get(0))) {
                parsedWord.append("('").append(unparsedSyllables.get(0)).append(')').append(unparsedSyllables.get(1));
                this.output = parsedWord.toString();
                return;
            }

            parsedWord.append("('").append(unparsedSyllables.get(0)).append(unparsedSyllables.get(1)).append(')');
            this.output = parsedWord.toString();
            return;
        }

        for (int unparsedSyllableIndex = unparsedSyllables.size() - 2; unparsedSyllableIndex >= 0; unparsedSyllableIndex--) {
            if (this.isHeavySyllable(unparsedSyllables.get(unparsedSyllableIndex))) {
                parsedWord.insert(this.rootLocation, "('" + unparsedSyllables.get(unparsedSyllableIndex) + ")");

                if (unparsedSyllables.size() == 4) {
                    parsedWord.insert(this.rootLocation, unparsedSyllables.get(0) + unparsedSyllables.get(1));
                    break;
                }

                primaryStressParsed = true;
                continue;
            }

            if (unparsedSyllableIndex - 1 >= 0 && this.isHeavySyllable(unparsedSyllables.get(unparsedSyllableIndex - 1))) {
                parsedWord.insert(this.rootLocation, unparsedSyllables.get(unparsedSyllableIndex));
                continue;
            }

            if (!unparsedLightSyllable.isEmpty()) {
                if (!primaryStressParsed) {
                    parsedWord.insert(this.rootLocation, "('" + unparsedSyllables.get(unparsedSyllableIndex) + unparsedLightSyllable + ")");
                }

                if (primaryStressParsed) {
                    parsedWord.insert(this.rootLocation, "(ˌ" + unparsedSyllables.get(unparsedSyllableIndex) + unparsedLightSyllable + ")");
                }

                primaryStressParsed = true;

                if (unparsedSyllableIndex == 0) {
                    break;
                }
                unparsedLightSyllable = "";
                continue;
            }

            if (unparsedSyllableIndex > 0) {
                unparsedLightSyllable = unparsedSyllables.get(unparsedSyllableIndex);
                continue;
            }

            parsedWord.insert(this.rootLocation, unparsedSyllables.get(unparsedSyllableIndex));
        }

        parsedWord.append(this.syllables.get(this.syllables.size() - 1));
        this.output = parsedWord.toString();
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }
    private boolean isGlide(char c) {
        return this.isLetter(c) && (c == 'j' || c == 'w');
    }

    private boolean isLongVowelIndicator(char c) {
        return c == ':';
    }

    private boolean isNotVowel(char c) {
        for (char vowel : this.vowels) {
            if (c == vowel) {
                return false;
            }
        }
        return true;
    }

    private boolean isHeavySyllable(String syllable) {
        return syllable.contains(":") || syllable.contains("j") || syllable.contains("w");
    }

    public String getOutput() {
        return this.output;
    }
}