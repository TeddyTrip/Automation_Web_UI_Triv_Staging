# Cara Mengambil Selector Web — Chrome DevTools

Dokumen ini dipakai untuk finalisasi locator Selenium pada halaman Futures.

## 1. Buka halaman target

Staging:

```text
https://cihuy.triv.id/id/futures/BTCUSDT-PERP
```

Production:

```text
https://triv.co.id/id/futures/BTCUSDT-PERP
```

## 2. Buka Chrome DevTools

- Klik kanan pada element yang ingin diambil selector-nya.
- Pilih **Inspect**.
- Tab **Elements** akan otomatis menunjuk HTML element tersebut.

Shortcut:

- macOS: `Command + Option + I`
- Windows: `F12` atau `Ctrl + Shift + I`

## 3. Untuk Search Bar

Klik kanan tepat pada input **Search** lalu Inspect.

Contoh HTML yang bagus untuk automation:

```html
<input id="market-search" data-testid="futures-market-search" placeholder="Search">
```

Urutan selector yang direkomendasikan:

1. `data-testid` / `data-qa`
2. `id`
3. `name`
4. stable CSS attribute
5. stable XPath
6. text-based XPath sebagai fallback

Contoh pilihan:

```text
CSS  : [data-testid='futures-market-search']
ID   : market-search
CSS  : input[placeholder='Search']
XPath: //input[@placeholder='Search']
```

> Hindari selector yang penuh `div:nth-child(...)` karena mudah berubah ketika layout berubah.

## 4. Cara cepat melihat attribute element

Saat element sedang terseleksi pada tab Elements, buka tab Console dan jalankan:

```javascript
$0.outerHTML
```

Atau:

```javascript
Array.from($0.attributes).map(a => `${a.name}=${a.value}`)
```

Kirim hasil `$0.outerHTML` ke automation maintainer untuk dipilih locator yang paling stabil.

## 5. Copy Selector / XPath

Pada tab Elements:

```text
Right Click element
→ Copy
→ Copy selector
```

atau:

```text
Right Click element
→ Copy
→ Copy XPath
```

Hasil ini boleh dikirim sebagai referensi, tetapi jangan otomatis dianggap selector final. Chrome sering menghasilkan selector yang terlalu panjang atau positional.

## 6. Selector yang dibutuhkan untuk Futures V1

Minimal kirim dua element berikut:

### A. Search Input

Kirim:

```text
Search input outerHTML
```

### B. Satu row/result Pair

Setelah search `BTCUSDT-PERP`, inspect text/result `BTCUSDT-PERP` pada tabel/list Pair.

Kirim:

```text
Pair result outerHTML
```

Kalau element Pair punya parent row yang menyimpan attribute symbol, kirim parent juga:

```javascript
$0.parentElement.outerHTML
```

atau naik beberapa level:

```javascript
$0.closest('tr')?.outerHTML
```

## 7. Cara memasukkan selector ke project

File:

```text
src/test/resources/config/config.properties
```

Default template saat ini:

```properties
futures.search.selector.type=xpath
futures.search.selector.value=//input[@placeholder='Search']
futures.pair.selector.template=//*[normalize-space(text())=%s]
```

Jika Search ternyata memiliki `data-testid`:

```properties
futures.search.selector.type=css
futures.search.selector.value=[data-testid='futures-market-search']
```

Tidak perlu edit `FuturesMarketPage.java`.

## 8. Test selector langsung dari DevTools

XPath:

```javascript
$x("//input[@placeholder='Search']")
```

Expected:

```text
1 element
```

CSS:

```javascript
document.querySelectorAll("input[placeholder='Search']")
```

Expected:

```text
NodeList(1)
```

Jika hasil lebih dari satu, selector perlu dibuat lebih spesifik.
