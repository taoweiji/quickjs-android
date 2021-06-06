package com.quickjs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ModuleTest {

    private final String expected;
    private final String parent;
    private final String path;

    @Parameterized.Parameters
    public static Collection<Object[]> t() {
        String parent = "pages/home/index.js";
        List<Object[]> children = new ArrayList<>();
        children.add(new String[]{parent, "./a.js", "pages/home/a.js"});
        children.add(new String[]{parent, "a.js", "pages/home/a.js"});

        children.add(new String[]{parent, "../a.js", "pages/a.js"});
        children.add(new String[]{parent, "./../a.js", "pages/a.js"});

        children.add(new String[]{parent, "../../a.js", "a.js"});
        children.add(new String[]{parent, "./../../a.js", "a.js"});

        children.add(new String[]{parent, "../tab1/a.js", "pages/tab1/a.js"});
        children.add(new String[]{parent, "../../tab1/a.js", "tab1/a.js"});

        children.add(new String[]{parent, "/tab1/a.js", "/tab1/a.js"});
        children.add(new String[]{parent, null, null});
        children.add(new String[]{null, "tab1/a.js", "tab1/a.js"});
        children.add(new String[]{"/", "tab1/a.js", "/tab1/a.js"});
        children.add(new String[]{"/user/", "tab1/a.js", "/user/tab1/a.js"});
        children.add(new String[]{"user/", "tab1/a.js", "user/tab1/a.js"});
        children.add(new String[]{null, "./tab1/a.js", "tab1/a.js"});

        return children;
    }

    public ModuleTest(String parent, String path, String expected) {
        this.expected = expected;
        this.parent = parent;
        this.path = path;
    }

    @Test
    public void test() {
        assertEquals(expected, convertModuleName(parent, path));
    }

    protected String convertModuleName(String moduleBaseName, String moduleName) {
        if (moduleName == null || moduleName.length() == 0) {
            return moduleName;
        }
        moduleName = moduleName.replace("//", "/");
        if (moduleName.startsWith("./")) {
            moduleName = moduleName.substring(2);
        }
        if (moduleName.charAt(0) == '/') {
            return moduleName;
        }
        if (moduleBaseName == null || moduleBaseName.length() == 0) {
            return moduleName;
        }
        moduleBaseName = moduleBaseName.replace("//", "/");
        if (moduleBaseName.startsWith("./")) {
            moduleBaseName = moduleBaseName.substring(2);
        }
        if (moduleBaseName.equals("/")) {
            return "/" + moduleName;
        }
        if (moduleBaseName.endsWith("/")) {
            return moduleBaseName + moduleName;
        }
        String[] parentSplit = moduleBaseName.split("/");
        String[] pathSplit = moduleName.split("/");
        List<String> parentStack = new ArrayList<>();
        List<String> pathStack = new ArrayList<>();
        Collections.addAll(parentStack, parentSplit);
        Collections.addAll(pathStack, pathSplit);
        while (!pathStack.isEmpty()) {
            String tmp = pathStack.get(0);
            if (tmp.equals("..")) {
                pathStack.remove(0);
                parentStack.remove(parentStack.size() - 1);
            } else {
                break;
            }
        }
        if (!parentStack.isEmpty()) {
            parentStack.remove(parentStack.size() - 1);
        }
        StringBuilder builder = new StringBuilder();
        if (moduleBaseName.startsWith("/")) {
            builder.append("/");
        }
        for (String it : parentStack) {
            builder.append(it).append("/");
        }
        for (String it : pathStack) {
            builder.append(it).append("/");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}