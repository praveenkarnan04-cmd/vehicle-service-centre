package vsc.ui.swing;

import vsc.exception.VscException;

@FunctionalInterface
interface ThrowingAction { void run() throws VscException; }
