package com.app.android.quickbud.modelClasses;

import java.util.ArrayList;

/**
 * Created by mobi11 on 31/12/15.
 */
public class TenantModel {

    private String tenantName = null;
    private ArrayList<TenantModel> tenantModels;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public ArrayList<TenantModel> getTenantModels() {
        return tenantModels;
    }

    public void setTenantModels(ArrayList<TenantModel> tenantModels) {
        this.tenantModels = tenantModels;
    }
}
