package com.top.arch.logger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Used to determine how messages should be printed or saved.
 *
 * @see com.orhanobut.logger.PrettyFormatStrategy
 * @see com.orhanobut.logger.CsvFormatStrategy
 */
public interface FormatStrategy {

  void log(int priority, @Nullable String tag, @NonNull String message);
}
