/// SharedViewModel.java
/// Prompting: Guy S, on 4o-mini-high
// Purpose: share name, family name, best_score,
// across fragments and across sessions (persist data)
/// https://chatgpt.com/share/68034575-9bcc-800e-abcb-86be4ecae071
/// personal link:https://chatgpt.com/c/6802507d-21c4-800e-9102-12ad1933807a
package com.example.guysassignment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

public class SharedViewModel extends AndroidViewModel {
    private static final String PREFS = "user_prefs";
    private static final String KEY_NAME = "name";
    private static final String KEY_FAMILY = "family_name";
    private static final String KEY_SCORE = "best_score";
    private final MutableLiveData<Integer> bestScoreX10 = new MutableLiveData<>();

    private final SharedPreferences prefs;
    private final MutableLiveData<String> name = new MutableLiveData<>();
    private final MutableLiveData<String> familyName = new MutableLiveData<>();
    private final MutableLiveData<Integer> bestScore = new MutableLiveData<>();

    public LiveData<Integer> getBestScoreX10() {
        return bestScoreX10;
    }

    public void setBestScoreX10(int scoreX10) {
        prefs.edit().putInt(KEY_SCORE, scoreX10).apply();
        bestScoreX10.setValue(scoreX10);
    }

    public SharedViewModel(@NonNull Application app) {
        super(app);
        prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        // Load stored value or default to 0
        bestScoreX10.setValue(prefs.getInt(KEY_SCORE, 0));

        // initialize from prefs (if missing, default to empty / 0)
        name.setValue(prefs.getString(KEY_NAME, "— no name set —"));
        familyName.setValue(prefs.getString(KEY_FAMILY, "— no family name set —"));
        bestScore.setValue(prefs.getInt(KEY_SCORE, -1));




        // listen for external edits (if any)
        prefs.registerOnSharedPreferenceChangeListener((sp, key) -> {
            switch (Objects.requireNonNull(key)) {
                case KEY_NAME:
                    name.postValue(sp.getString(KEY_NAME, ""));
                    break;
                case KEY_FAMILY:
                    familyName.postValue(sp.getString(KEY_FAMILY, ""));
                    break;
                case KEY_SCORE:
                    bestScore.postValue(sp.getInt(KEY_SCORE, 0));
                    break;
            }
        });
    }

    // Expose LiveData so fragments can observe
    public LiveData<String> getName() {
        return name;
    }

    public LiveData<String> getFamilyName() {
        return familyName;
    }

    // Call these to update & persist
    public void setName(String newName) {
        prefs.edit().putString(KEY_NAME, newName).apply();
    }

    public void setFamilyName(String newFamily) {
        prefs.edit().putString(KEY_FAMILY, newFamily).apply();
    }

}
