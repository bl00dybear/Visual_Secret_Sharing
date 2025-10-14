# Visual Secret Sharing (VSS)

Visual cryptography project implementing Naor–Shamir-style visual secret sharing on images, with a layered Java/JavaFX architecture (View–Controller–Service–Model), authentication, and a simple Oracle XE Docker setup for user management.

## Quick start (Oracle XE in Docker)

```bash
docker build -t vss-oracle-db .
```

```bash
docker run -d \
  --name oracle-db-vss \
  -p 1521:1521 \
  -p 5500:5500 \
  --restart unless-stopped \
  vss-oracle-db
```

```bash
docker exec -it oracle-db-vss bash -c "source /home/oracle/.bashrc; sqlplus / as sysdba"
```

```bash
docker exec -it oracle-db-vss bash -c "source /home/oracle/.bashrc; sqlplus system/Parola1\!@XEPDB1"
```

Note: Application code is Java/JavaFX. The database is only used for user/auth flows (e.g., login and password update).

## Theoretical foundation: Shamir’s Secret Sharing

Goal: Split a secret S into n parts (shares) so that any k shares reconstruct S, while any set with fewer than k shares reveals nothing.

- Choose a prime field $$F_p$$ with sufficiently large p.
- Sample a random polynomial of degree k−1:
  $$f(x) = s + a_1x + a_2x^2 + \dots + a_{k-1}x^{k-1} \pmod p,$$
  where s ∈ F_p is the secret and a_i are uniform in $$F_p$$.
- For i = 1..n, issue share (x_i, f(x_i)) with distinct non-zero $$x_i$$.

Reconstruction uses Lagrange interpolation with any k points:
```math
s = f(0) = \sum_{j=1}^{k} y_j \cdot \prod_{\substack{m=1\\m\neq j}}^{k}\frac{-x_m}{x_j-x_m} \ (\bmod\ p).
```

Properties:
- Information-theoretic secrecy for any subset of size < k.
- Deterministic reconstruction from any k valid shares.

Relation to Visual Secret Sharing (Naor–Shamir): the k-of-n threshold concept is adapted to images; reconstruction is visual (overlaying transparencies) rather than numerical interpolation.

## Visual Secret Sharing used here (Naor–Shamir-style)

Concept: A binary image is split into n shares such that:
- Any k valid shares, when overlaid, reveal the original image visually (lower contrast; sufficient to read).
- Any set with < k shares yields no information about the original.

Typical VSS pipeline:
- Preprocess image: load, grayscale/binarize, normalize (handled by service layer).
- Generate shares: convert each source pixel into subpixel blocks on each share using randomized basis patterns consistent with VSS (e.g., 2-of-2 uses complementary 2×2 subpixel tiles; k-of-n uses families of matrices ensuring contrast when ≥k overlays).
- Persist shares: write to disk (e.g., the shares/ folder) with metadata.
- Recombine: load ≥k shares and overlay them (logical OR/AND on binary subpixels) to reconstruct.

Security:
- Each share alone is statistically independent from the source image.
- No cryptographic key to keep; secrecy emerges from combinatorial design and randomness.

Trade-offs:
- Pixel expansion: shares are larger than the source (due to subpixels).
- Contrast vs expansion: more expansion often yields better contrast on reconstruction.

## Architecture

Layers and responsibilities:

- View (JavaFX)
  - `view.InterfaceManager`: screen routing and stage/scene orchestration.
  - `view.screens.*`: `LoginScreen`, `MainScreen`, `EncryptScreen`, `DecryptScreen`, `UpdatePasswordScreen`.
  - `view.components.UIComponents`: shared UI builders/components.
  - Styling: `resources/styles.css`.

- Controller
  - `controller.AuthController`: authentication and user actions (e.g., password update), bridges UI to `AuthService`.
  - `controller.EncryptController`: drives preprocessing and share generation.
  - `controller.DecryptController`: drives share combination.

- Service
  - `application.service.AuthService`: login, password update, user ops.
  - `application.service.SecretService`: image loading and preprocessing (binarization/normalization).
  - `application.service.ShareService`: share generation and combination (VSS).
  - `application.service.DatabaseService`: database connectivity and user storage.

- Model
  - `model.ImageData`: in-memory image data and metadata.
  - `model.Secret`: preprocessed representation of the visual secret.
  - `model.Share`: share representation and metadata (index, path, format).

- Observer
  - `observer.ImageProcessingObserver`: progress/error callbacks during image processing/generation.

## Design patterns

- MVC layering: explicit separation of View, Controller, Model, and Service.
- Facade: controllers expose minimal, cohesive methods to the View and hide service orchestration.
- Observer: `ImageProcessingObserver` decouples processing pipelines from UI progress feedback.
- Factory/Builder (UI): `UIComponents` centralizes consistent control construction/styling.
- Coordinator: `InterfaceManager` centralizes navigation and screen lifecycle.
- Singleton-like resource: `DatabaseService` typically provides a single entry point for DB connections.

## Application flows

- Authentication:
  - `LoginScreen` → `AuthController.login` → `AuthService` → `DatabaseService` → result; on success, `InterfaceManager.showMainScreen`.
- Encrypt:
  - `MainScreen` → `EncryptScreen` → `EncryptController.encrypt`.
  - `SecretService`: load + binarize.
  - `ShareService`: generate n shares, write to disk, emit progress via `ImageProcessingObserver`.
- Decrypt:
  - `MainScreen` → `DecryptScreen` → select ≥k shares → `DecryptController.decrypt`.
  - `ShareService`: validate and overlay; write reconstructed image to disk.
- Update password:
  - `MainScreen` → `UpdatePasswordScreen` → `AuthController.updatePassword(old,new)` → `AuthService` → `DatabaseService`.

## Class diagram

```text
+---------------------------------------------------------------------------------------------+
|                                          VIEW                                               |
+---------------------------------------------------------------------------------------------+
|                                  +-----------------------+                                  |
|                                  |   InterfaceManager    |                                  |
|                                  +-----------------------+                                  |
|                                  | - primaryStage        |                                  |
|                                  | - authController      |                                  |
|                                  | - encryptController   |                                  |
|                                  | - decryptController   |                                  |
|                                  +-----------+-----------+                                  |
|                                              | shows                                        |
|     +-------------------+   +-----------------+-----------------+   +-------------------+   |
|     |    LoginScreen    |   |     MainScreen                    |   | UpdatePasswordScr |   |
|     +-------------------+   +-----------------+-----------------+   +-------------------+   |
|               | uses                     | uses                                  | uses     |
|     +-------------------+   +-----------------------------+         +-------------------+   |
|     |  EncryptScreen    |   |       DecryptScreen         |         |   UIComponents    |   |
|     +-------------------+   +-----------------------------+         +-------------------+   |
+---------------------------------------------------------------------------------------------+

+-----------------------------------+      +-----------------------------------+      +------------------+
|             CONTROLLERS           |      |               SERVICES            |      |      MODEL       |
+-----------------------------------+      +-----------------------------------+      +------------------+
| +-------------------------------+ |      | +-------------------------------+ |      | +--------------+ |
| |        AuthController         | | uses | |          AuthService          | |----->| |    User      | | (if present)
| +-------------------------------+ |----->| +-------------------------------+ |      | +--------------+ |
|                                   |      |                | uses             |      | +--------------+ |
| +-------------------------------+ |      |                v                  |      | |   Secret     | |
| |      EncryptController        | |----->| +-------------------------------+ |      | +--------------+ |
| +-------------------------------+ | uses | |         SecretService         | |----->| +--------------+ |
|                                   |      | +-------------------------------+ |      | |  ImageData   | |
| +-------------------------------+ |      |                | uses             |      | +--------------+ |
| |       DecryptController       | |----->|                v                  |      | +--------------+ |
| +-------------------------------+ |      | +-------------------------------+ |      | |    Share     | |
|                                   |      | |          ShareService         | |----->| +--------------+ |
|                                   |      | +-------------------------------+ |      |                  |
+-----------------------------------+      |                | uses             |      +------------------+
                                           |                v                  |
                                           | +-------------------------------+ |
                                           | |        DatabaseService        | |
                                           | +-------------------------------+ |
                                           +-----------------------------------+

+-----------------------------+
|          OBSERVER           |
+-----------------------------+
| ImageProcessingObserver     | <---- used by SecretService/ShareService
+-----------------------------+
```

Legend:
- “uses” arrows show direction of dependency.
- `InterfaceManager` coordinates screens and owns controllers.
- Controllers hide multi-service orchestration from the View.
- Services operate on Models and the database.

## Flow diagrams

### Authentication (login)

```text
LoginScreen
   |
   v
AuthController.login(user, pass)
   |
   v
AuthService.authenticate(user, pass)
   |
   v
DatabaseService.query+verify
   |
   v
result -> InterfaceManager.showMainScreen() on success
```

### Encrypt

```text
EncryptScreen
   |
   v
EncryptController.encrypt(imagePath, params)
   |
   +--> SecretService.load+binarize -> Secret/ImageData
   |
   +--> ShareService.generateShares(Secret, k, n)
             |
             +--> ImageProcessingObserver (progress)
             |
             +--> write shares/ to disk -> return share paths
```

### Decrypt

```text
DecryptScreen
   |
   v
DecryptController.decrypt(selectedShares>=k)
   |
   v
ShareService.combine(selectedShares)
   |
   +--> overlay/compose -> reconstructed image -> write to disk (e.g., test_output/)
```

### Update Password

```text
UpdatePasswordScreen
   |
   v
AuthController.updatePassword(old, new)
   |
   v
AuthService.updatePassword
   |
   v
DatabaseService.update users set password=...
```

## Algorithm details (implementation-specific)

This project implements per-pixel Shamir Secret Sharing over the finite field GF(257), not Naor–Shamir visual overlays.

- Field: GF(257). 257 is prime and > 255, making it suitable to encode 8-bit channel values in a true field.
- Threshold parameters:
  - n = total number of shares (totalShares)
  - k = minimum shares required (minShares)
- Share indices: x ∈ {1, 2, ..., n}. The filename encodes the index as share_<x>.png.

Encoding (share generation)

- For each pixel (i, j) and each RGB channel z ∈ {0,1,2}, let s be the original channel value (0..255).
- Construct a random polynomial f(x) of degree k−1 over GF(257):
  - a0 = s
  - a1..a_{k−1} are sampled uniformly from 1..256
  - f(x) = a0 + a1 x + a2 x^2 + ... + a_{k−1} x^{k−1} (mod 257)
- For each share index x = 1..n:
  - Compute y = f(x) mod 257
  - Set the share image’s pixel channel to y
- Output shares are standard 24-bit images where each channel holds its corresponding field element y (0..256). Files are named share_<x>.png to preserve the share index.

Decoding (secret reconstruction)

- Input: any k share images share_<x1>.png, ..., share_<xk>.png with the same width/height as the original.
- Parse indices X = {x1, ..., xk} from filenames; sort them; for each pixel/channel read y1..yk from the k share images.
- Reconstruct s = f(0) via Lagrange interpolation in GF(257):
  - For each j ∈ {1..k}, compute
    Lj = ∏(m≠j) (x_m / (x_j − x_m)) mod 257
    where division is modular inverse via Extended Euclid.
  - s' = Σ(j=1..k) (y_j · Lj) mod 257
- Post-processing:
  - If s' == 256, clamp to 255 (to fit 8-bit channel range).
  - Set reconstructed pixel channel to s' (now in 0..255).

Operational details observed in code

- The encoder operates channel-wise for each pixel; coefficients are freshly randomized per pixel/channel for information-theoretic secrecy.
- Share pixel/channel values are direct field elements; some may be 256 pre-encoding. During reconstruction, 256 is normalized to 255; share writing does not clamp explicitly.
- File naming is part of the contract. Indices are derived from the “share_<index>.png” pattern. Wrong or missing indices break correct interpolation.
- Any set with fewer than k shares is insufficient; the code will still compute an interpolation, but the result will not match the original (mathematically underdetermined).
- After encryption, the original secret image is stored in the database (bytes). After decryption, the reconstructed image is compared against the stored one using either byte equality or SHA-256 for verification; results are logged.

Complexity

- Encoding: O(n · k · W · H · 3) across width W, height H, and 3 channels, due to polynomial evaluation per share.
- Decoding: O(k^2 · W · H · 3) with naive Lagrange coefficient computation per pixel/channel; k is typically small.

Edge conditions and constraints

- Channel domain: Original channels are 0..255; field operations are mod 257. The single extra element (256) is handled by clamping s'==256→255 at decode.
- Image dimensions: All shares used for reconstruction must exactly match the original dimensions and each other.
- Indexing: Only shares with indices in 1..n are valid relative to how encoding was performed; filenames supply these indices.
- Correctness threshold: At least k valid, correctly indexed shares are required to recover the exact original image.

## Security and limitations

- Information-theoretic secrecy for any subset of < k shares.
- No private key management; shares themselves are the only sensitive artifacts.
- Lower contrast on reconstruction versus source; tunable via pixel expansion.
- Larger output images (expansion).
- Operational risk: compromise occurs if an attacker aggregates ≥k shares.


## Differentiators

- Clean layered MVC with controllers as facades and explicit service boundaries.
- Visual Secret Sharing implemented with image preprocessing and share composition, ready for extension.
- Observer-based progress reporting for long-running image operations.
- Oracle XE Docker recipe for reproducible auth storage.
- Password update flow wired through View → Controller → Service → DB.

## Note

  This documentation was created using an LLM.
