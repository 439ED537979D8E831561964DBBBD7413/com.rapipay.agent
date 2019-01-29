package com.rapipay.android.agent.interfaces;

import com.rapipay.android.agent.Model.VersionPozo;

import java.util.ArrayList;

public interface VersionListener {
    void checkVersion(ArrayList<VersionPozo> list);
}
