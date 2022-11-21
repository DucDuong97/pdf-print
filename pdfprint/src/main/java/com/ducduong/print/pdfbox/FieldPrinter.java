package com.ducduong.print.pdfbox;

import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.*;

import com.ducduong.print.crf.pdfquestion.Button;
import com.ducduong.print.util.PdfPrintable;
import com.ducduong.print.util.ResourceProvider;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.ducduong.print.SharedConfiguration.*;

public class FieldPrinter {

    private static final List<String> HOURS = new ArrayList<>();
    private static final List<String> MINUTES = new ArrayList<>();
    private static final List<String> SECONDS = new ArrayList<>();
    private static final List<String> DAYS = new ArrayList<>();
    private static final List<String> MONTHS = new ArrayList<>();
    private static final List<String> YEARS = new ArrayList<>();

    static {
        for (int i = 0; i < 31; i++) {
            DAYS.add(String.valueOf(i+1));
        }
        for (int i = 0; i < 12; i++) {
            MONTHS.add(String.valueOf(i+1));
        }
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i > 1900; i--) {
            YEARS.add(String.valueOf(i));
        }
        for (int i = 0; i < 24; i++) {
            HOURS.add(String.valueOf(i+1));
        }
        for (int i = 0; i < 60; i++) {
            MINUTES.add(String.valueOf(i+1));
            SECONDS.add(String.valueOf(i+1));
        }
    }

    private final PDAcroForm currentForm;
    private final PdfPrintable<String> textPrinter;
    private final PdfWidgetHandler widgetHandler;

    private final Set<String> fieldNameSet;

    public FieldPrinter(ResourceProvider resourceProvider, PdfPrintable<String> textPrinter) {
        PDFBoxResourceProvider pdfBoxResourceProvider = (PDFBoxResourceProvider) resourceProvider;
        this.currentForm = pdfBoxResourceProvider.provideForm();
        this.textPrinter = textPrinter;
        this.widgetHandler = new PdfWidgetHandler(pdfBoxResourceProvider);
        fieldNameSet = new HashSet<>();
    }

    public void addField(String name, String defaultValue, float x, float y, boolean readOnly, Integer maxLen) {
        try {
            // Add a form field to the form.
            name = specifyQuestionName(name);
            PDTextField textBox = new PDTextField(currentForm);
            currentForm.getFields().add(textBox);
            textBox.setPartialName(name);
            textBox.setReadOnly(readOnly);
            textBox.setMaxLen(Objects.requireNonNullElse(maxLen, LETTERS_PER_LINE));

            int numOfLines = 1;
            if (maxLen != null && maxLen > LETTERS_PER_LINE) {
                textBox.setMultiline(true);
                numOfLines = (maxLen % LETTERS_PER_LINE == 0 ? 0 : 1) + maxLen / LETTERS_PER_LINE;
            }

            // Specify the widget annotation associated with the field
            widgetHandler.setupFieldWidget(textBox, x, y - FIELD_HEIGHT * numOfLines,
                    FIELD_WIDTH, FIELD_HEIGHT * numOfLines);
            // Add the widget annotation to the page

            // set the field value
            textBox.setValue(defaultValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int addDropdownMenu(String name, String defaultValue, float x, float y, boolean readOnly, List<String> values) {
        name = specifyQuestionName(name);
        try {
            PDComboBox comboBox = new PDComboBox(currentForm);
            currentForm.getFields().add(comboBox);
            comboBox.setPartialName(name);
            comboBox.setDefaultValue(defaultValue);
            comboBox.setReadOnly(readOnly);
            comboBox.setOptions(values);
            comboBox.setMultiSelect(false);

            // Specify the widget annotation associated with the field
            // calculate box width
            int maxValueWidth = values.stream()
                    .map(textPrinter::calculateWidth)
                    .max(Comparator.naturalOrder())
                    .orElse(50.0f)
                    .intValue();
            int boxWidth = Math.min(maxValueWidth + 30, FIELD_WIDTH);
            widgetHandler.setupFieldWidget(comboBox, x, y - FIELD_HEIGHT, boxWidth, FIELD_HEIGHT);
            // Add the widget annotation to the page
            return boxWidth;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Generate Form Check Boxes
    public float addCheckBox(String name, float x, float y, boolean readOnly, String defaultValue) {

        name = specifyQuestionName(name);
        PDCheckBox checkbox = new PDCheckBox(currentForm);
        currentForm.getFields().add(checkbox);

        checkbox.setPartialName(name);
        checkbox.setReadOnly(readOnly);
        widgetHandler.setupCheckboxWidget(checkbox, x, y - OPTION_BOX_SIZE, OPTION_BOX_SIZE);

        boolean checked = Boolean.parseBoolean(defaultValue);
        try {
            if (checked){
                checkbox.check();
            } else {
                checkbox.unCheck();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        textPrinter.print(name, OPTION_WIDTH, x + OPTION_BOX_SIZE + OPTION_OFFSET, y);
        return textPrinter.calculateHeight(name, OPTION_WIDTH);
    }

    // Generate Form Radio Buttons
    public void addRadioButtons(String question, String defaultValue, float x, float y, boolean readOnly, List<Button> buttonList) {

        question = specifyQuestionName(question);
        try {
            PDRadioButton radioButton = new PDRadioButton(currentForm);
            currentForm.getFields().add(radioButton);

            radioButton.setPartialName(question);
            radioButton.setReadOnly(readOnly);
            radioButton.setExportValues(buttonList.stream().map(Button::getValue).collect(Collectors.toList()));

            final float optionX = x + OPTION_BOX_SIZE + OPTION_OFFSET;
            float localHeight = y;
            List<PDAnnotationWidget> widgets = new ArrayList<>();
            for (Button button : buttonList) {
                PDAnnotationWidget widget = widgetHandler.setupRadioButtonWidget(x,
                        localHeight - OPTION_BOX_SIZE, OPTION_BOX_SIZE, specifyQuestionName(button.getValue()));
                widgets.add(widget);
                textPrinter.print(button.getName(), OPTION_WIDTH, optionX, localHeight);
                localHeight -= textPrinter.calculateHeight(button.getName(), OPTION_WIDTH);
            }
            radioButton.setWidgets(widgets);
            if (!defaultValue.equals("")) {
                radioButton.setValue(defaultValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDateDropDown(String question, String defaultValue, float x, float y) {
        List<String> values = Arrays.asList(defaultValue.split("-"));
        if (values.size() != 3) {
            values = new ArrayList<>();
            values.add("");
            values.add("");
            values.add("");
        }
        question = specifyQuestionName(question);
        textPrinter.print("Year:", 100, x, y);
        x += textPrinter.calculateWidth("Year: ");
        x += addDropdownMenu(question + "_year", values.get(0), x, y, false, YEARS) + 5;
        textPrinter.print("Month:", 100, x, y);
        x += textPrinter.calculateWidth("Month: ");
        x += addDropdownMenu(question + "_month", values.get(1), x, y, false, MONTHS) + 5;
        textPrinter.print("Day:", 100, x, y);
        x += textPrinter.calculateWidth("Day: ");
        x += addDropdownMenu(question + "_day", values.get(2), x, y, false, DAYS);
    }

    public void addTimeDropDown(String question, String defaultValue, float x, float y) {

        question = specifyQuestionName(question);
        //        String[] defaultValues = defaultValue.split(":");
        textPrinter.print("Hour:", 100, x, y);
        x += textPrinter.calculateWidth("Hour: ");
        x += addDropdownMenu(question + "_hour", "", x, y, false, HOURS) + 5;
        textPrinter.print("Minute:", 100, x, y);
        x += textPrinter.calculateWidth("Minute: ");
        x += addDropdownMenu(question + "_minute", "", x, y, false, MINUTES) + 5;
        textPrinter.print("Second:", 100, x, y);
        x += textPrinter.calculateWidth("Second: ");
        x += addDropdownMenu(question + "_second", "", x, y, false, SECONDS);
    }

    public String specifyQuestionName(String question) {
        // Field's name mustn't have any '.'
        String result = question.replace('.', '*');
        // make sure checkbox values are unique
        while (fieldNameSet.contains(result)) {
            result = result + " ";
        }
        fieldNameSet.add(result);
        return result;
    }
}
