# Future AI Editor — মোবাইল থেকে APK বানানোর প্রজেক্ট

## গুরুত্বপূর্ণ
এই ZIP-এ Android অ্যাপের সোর্স কোড এবং GitHub Actions workflow আছে। এখানে APK আগে থেকে বানানো বা পরীক্ষা করা নেই। GitHub-এর ক্লাউড বিল্ড ব্যবহার করে Android ফোন থেকেই APK বানানো যাবে; কম্পিউটার লাগবে না।

## ফোন দিয়ে APK বানানোর ধাপ

1. ZIP ফাইলটি ফোনে Extract করো।
2. GitHub অ্যাকাউন্ট না থাকলে github.com-এ অ্যাকাউন্ট তৈরি করো এবং সাইন ইন করো।
3. GitHub-এ `FutureAIEditor` নামে একটি **নতুন Public repository** তৈরি করো।
4. Extract করা প্রজেক্টের সব ফাইল ও ফোল্ডার repository-তে আপলোড করো। `.github/workflows/build-apk.yml` ফাইলটি অবশ্যই থাকতে হবে।
5. Repository-র **Actions** ট্যাবে যাও।
6. `Build Future AI Editor APK` workflow বেছে **Run workflow** চাপো।
7. Build শেষ হলে একই workflow run-এর নিচে **Artifacts** থেকে `FutureAIEditor-debug-apk` ডাউনলোড করো।
8. ZIP artifact Extract করে `app-debug.apk` ইনস্টল করো। Android চাইলে ওই ব্রাউজার/ফাইল ম্যানেজারের জন্য “Install unknown apps” অনুমতি দিতে হবে।

## এই সংস্করণে যা আছে
- ভিডিও picker: ফোনের গ্যালারি থেকে একটি ভিডিও নির্বাচন
- বাংলা/ইংরেজি স্ক্রিপ্ট ইনপুট
- বাক্য ভাগ করে সাধারণ দৃশ্য-পরিকল্পনা
- MP4 export (Media3 Transformer)
- 9:16, 16:9, 1:1 অনুপাত বেছে দেখানোর UI

## এখনো যা নেই
- AI দিয়ে নতুন ভিডিও/দৃশ্য তৈরি
- AI পুরুষ/নারী ভয়েস জেনারেশন বা voice cloning
- অটো-সাবটাইটেল ও শব্দের সঙ্গে subtitle timing
- ব্যাকগ্রাউন্ড মিউজিক মিক্সিং, একাধিক অডিও ট্র্যাক
- ফিল্টার, ট্রানজিশন, multi-track timeline
- 1080p/4K বা সোশ্যাল-মিডিয়া export presets
- UI-তে দেখানো aspect ratio বর্তমানে export-এ প্রয়োগ হয় না

## খরচ ও গোপনীয়তা
GitHub Actions-এর বিনামূল্যের সীমা ও অ্যাকাউন্ট নীতিমালা প্রযোজ্য; unlimited free build নিশ্চিত নয়। Repository Public করলে কোড সবার দেখা যাবে, তাই কোনো API key, পাসওয়ার্ড বা ব্যক্তিগত ফাইল আপলোড করবে না। APK debug build; Play Store release/signing configuration নয়।
