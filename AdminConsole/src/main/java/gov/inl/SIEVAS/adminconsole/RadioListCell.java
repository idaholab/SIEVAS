/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 *
 * @author monejh
 */
public class RadioListCell<T> extends ListCell<T> {
    private ToggleGroup group;
    private RadioButton radioButton;
    
    
    
    public RadioListCell(ToggleGroup group)
    {
        super();   
        this.group = group;
    }
    
    public RadioListCell(
            final Callback<T, ObservableValue<Boolean>> getSelectedProperty,
            final StringConverter<T> converter, ToggleGroup group) {
        this.getStyleClass().add("radio-box-list-cell");
        setSelectedStateCallback(getSelectedProperty);
        setConverter(converter);

        this.radioButton = new RadioButton();

        setAlignment(Pos.CENTER_LEFT);
        setContentDisplay(ContentDisplay.LEFT);

        // by default the graphic is null until the cell stops being empty
        setGraphic(null);
        this.group = group;
    }
    
    public void setToggleGroup(ToggleGroup group)
    {
        this.group = group;
    }
    public final Callback<T, ObservableValue<Boolean>> getSelectedStateCallback() {
        return selectedStateCallbackProperty().get();
    }
    
    private ObservableValue<Boolean> booleanProperty;
    
    
    @Override
    public void updateItem(T obj, boolean empty) {
        super.updateItem(obj, empty);
        if (! empty) {
            StringConverter<T> c = getConverter();
            Callback<T, ObservableValue<Boolean>> callback = getSelectedStateCallback();
            if (callback == null) {
                throw new NullPointerException(
                        "The CheckBoxListCell selectedStateCallbackProperty can not be null");
            }

            radioButton.setToggleGroup(group);
            radioButton.setText(getConverter().toString(obj));
            // Add Listeners if any
            setGraphic(radioButton);
            
            //setText(c != null ? c.toString(item) : (item == null ? "" : item.toString()));

            if (booleanProperty != null) {
                radioButton.selectedProperty().unbindBidirectional((BooleanProperty)booleanProperty);
            }
            booleanProperty = callback.call(obj);
            if (booleanProperty != null) {
                radioButton.selectedProperty().bindBidirectional((BooleanProperty)booleanProperty);
            }
        } else {
            setGraphic(null);
            setText(null);
        }
        
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            
        }
    }
    
    private ObjectProperty<StringConverter<T>> converter =
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");
    
    /**
     * The {@link StringConverter} property.
     */
    public final ObjectProperty<StringConverter<T>> converterProperty() {
        return converter;
    }

    /**
     * Sets the {@link StringConverter} to be used in this cell.
     */
    public final void setConverter(StringConverter<T> value) {
        converterProperty().set(value);
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     */
    public final StringConverter<T> getConverter() {
        return converterProperty().get();
    }
    
     /**
     * Sets the {@link Callback} that is bound to by the CheckBox shown on screen.
     */
    public final void setSelectedStateCallback(Callback<T, ObservableValue<Boolean>> value) {
        selectedStateCallbackProperty().set(value);
    }
    
    /**
     * Property representing the {@link Callback} that is bound to by the
     * CheckBox shown on screen.
     */
    public final ObjectProperty<Callback<T, ObservableValue<Boolean>>> selectedStateCallbackProperty() {
        return selectedStateCallback;
    }
    
    // --- selected state callback property
    private ObjectProperty<Callback<T, ObservableValue<Boolean>>>
            selectedStateCallback =
            new SimpleObjectProperty<Callback<T, ObservableValue<Boolean>>>(
            this, "selectedStateCallback");

    
    
    
    public static <T> Callback<ListView<T>, ListCell<T>> forListView(
            final Callback<T, ObservableValue<Boolean>> getSelectedProperty,
            final StringConverter<T> converter, final ToggleGroup group) {
        return list -> new RadioListCell<T>(getSelectedProperty, converter, group);
    }
}
