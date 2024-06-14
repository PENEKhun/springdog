/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.easypeelsecurity.springdog.autoconfigure.controller.parser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Extracts parameter names from a method with the help of ASM. This class helps you get the name of a parameter
 * without gradle's -parameter option.
 *
 * @author PENEKhun
 */
abstract class ParameterNameExtractor {

  /**
   * Extracts parameter names for all methods in the specified class.
   *
   * @param clazz the class to analyze
   * @return a map where the keys are method descriptors and the values are arrays of parameter names
   * @throws IOException if an I/O error occurs while reading the class bytecode
   */
  private static Map<String, String[]> getMethodParameterNames(Class<?> clazz) throws IOException {
    Map<String, String[]> methodParamNames = new HashMap<>();
    ClassReader classReader = new ClassReader(clazz.getName());
    ClassNode classNode = new ClassNode();
    classReader.accept(classNode, 0);

    List<MethodNode> methods = classNode.methods;
    for (MethodNode method : methods) {
      String[] paramNames = new String[Type.getArgumentTypes(method.desc).length];
      methodParamNames.put(method.name + method.desc, paramNames);
      method.accept(new MethodVisitor(Opcodes.ASM9) {
        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
          if (descriptor.equals(Type.getDescriptor(RequestParam.class))) {
            return new AnnotationVisitor(Opcodes.ASM9) {
              @Override
              public void visit(String name, Object value) {
                if ("value".equals(name)) {
                  paramNames[parameter] = (String) value;
                }
              }
            };
          }
          return super.visitParameterAnnotation(parameter, descriptor, visible);
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
            int index) {
          int paramIndex = index - 1; // Skip 'this' reference for non-static methods
          if (paramIndex >= 0 && paramIndex < paramNames.length && paramNames[paramIndex] == null) {
            paramNames[paramIndex] = name;
          }
        }
      });
    }
    return methodParamNames;
  }

  /**
   * Gets the parameter names for a specific method in the specified class.
   *
   * @param clazz          the class containing the method
   * @param methodName     the name of the method
   * @param parameterTypes the parameter types of the method
   * @return an array of parameter names
   * @throws IOException           if an I/O error occurs while reading the class bytecode
   * @throws NoSuchMethodException if the method cannot be found
   */
  public static String[] getParameterNames(Class<?> clazz, String methodName, Class<?>... parameterTypes)
      throws IOException, NoSuchMethodException {
    Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
    String key = method.getName() + Type.getMethodDescriptor(method);
    Map<String, String[]> paramMap = getMethodParameterNames(clazz);
    return paramMap.get(key);
  }
}
