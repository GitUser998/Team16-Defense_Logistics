package com.vit.model;

import java.util.ArrayList;
import java.util.List;

// details of one module (like Vehicles), so the same pages work for every module
public class ModuleInfo {

    private String key;
    private String title;
    private String baseUrl;
    private String idField;
    private boolean canEdit;
    private boolean canDelete;
    private List<String[]> columns = new ArrayList<>();
    private List<String[]> fields = new ArrayList<>();
    private List<String[]> actions = new ArrayList<>();

    public ModuleInfo(String key, String title, String baseUrl, String idField,
                      boolean canEdit, boolean canDelete) {
        this.key = key;
        this.title = title;
        this.baseUrl = baseUrl;
        this.idField = idField;
        this.canEdit = canEdit;
        this.canDelete = canDelete;
    }

    // a column shown in the table: name in JSON, heading
    public void addColumn(String name, String label) {
        columns.add(new String[] { name, label });
    }

    // a field in the add/edit form: name in JSON, label, type
    // type can be: text, optional, date, number, or select:A,B,C
    public void addField(String name, String label, String type) {
        fields.add(new String[] { name, label, type });
    }

    // an action on a row: name in the URL, label, name of the extra value it needs ("" if none)
    public void addAction(String name, String label, String paramName) {
        actions.add(new String[] { name, label, paramName });
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getIdField() {
        return idField;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public List<String[]> getColumns() {
        return columns;
    }

    public List<String[]> getFields() {
        return fields;
    }

    public List<String[]> getActions() {
        return actions;
    }
}