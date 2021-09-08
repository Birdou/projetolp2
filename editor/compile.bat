@echo off

mkdir com
cd com
javac -encoding utf-8 -d . ../*.java
jar -cfve ../Editor.jar editor.Editor editor/*.class