/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.image.setup;

import java.util.Arrays;

public enum StorageMode {

  DRIVE("Drive");

  public static final StorageMode STD_STORAGE_MODE = DRIVE;

  private final String strRepresentation;

  private StorageMode(String strRepresentation) {
    this.strRepresentation = strRepresentation;
  }

  public String getStrRepresentation() {
    return new String(strRepresentation);
  }

  public static StorageMode getStorageModeFromString(String strStorageMode) {
    return Arrays.asList(StorageMode.values()).stream()
        .filter(mode -> mode.strRepresentation.equals(strStorageMode)).findFirst()
        .orElse(STD_STORAGE_MODE);
  }
}
