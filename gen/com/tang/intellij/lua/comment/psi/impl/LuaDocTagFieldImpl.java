// This is a generated file. Not intended for manual editing.
package com.tang.intellij.lua.comment.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.tang.intellij.lua.comment.psi.LuaDocTypes.*;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.tang.intellij.lua.stubs.LuaDocTagFieldStub;
import com.tang.intellij.lua.comment.psi.*;
import com.intellij.navigation.ItemPresentation;
import com.tang.intellij.lua.psi.Visibility;
import com.tang.intellij.lua.search.SearchContext;
import com.tang.intellij.lua.ty.ITy;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.ApiStatus.Experimental;
import com.intellij.psi.tree.IElementType;

public class LuaDocTagFieldImpl extends StubBasedPsiElementBase<LuaDocTagFieldStub> implements LuaDocTagField {

  public LuaDocTagFieldImpl(@NotNull LuaDocTagFieldStub stub, @NotNull IStubElementType<?, ?> type) {
    super(stub, type);
  }

  public LuaDocTagFieldImpl(@NotNull LuaDocTagFieldStub stub, @NotNull IElementType type) {
    super(stub, type);
  }

  public LuaDocTagFieldImpl(@NotNull ASTNode node) {
    super(node);
  }

  public LuaDocTagFieldImpl(LuaDocTagFieldStub stub, IElementType type, ASTNode node) {
    super(stub, type, node);
  }

  public void accept(@NotNull LuaDocVisitor visitor) {
    visitor.visitTagField(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof LuaDocVisitor) accept((LuaDocVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public LuaDocAccessModifier getAccessModifier() {
    return PsiTreeUtil.getChildOfType(this, LuaDocAccessModifier.class);
  }

  @Override
  @Nullable
  public LuaDocClassNameRef getClassNameRef() {
    return PsiTreeUtil.getChildOfType(this, LuaDocClassNameRef.class);
  }

  @Override
  @Nullable
  public LuaDocCommentString getCommentString() {
    return PsiTreeUtil.getChildOfType(this, LuaDocCommentString.class);
  }

  @Override
  @Nullable
  public LuaDocTy getTy() {
    return PsiTreeUtil.getChildOfType(this, LuaDocTy.class);
  }

  @Override
  @Nullable
  public PsiElement getId() {
    return findChildByType(ID);
  }

  @Override
  public @NotNull ITy guessParentType(@NotNull SearchContext context) {
    return LuaDocPsiImplUtilKt.guessParentType(this, context);
  }

  @Override
  public @NotNull Visibility getVisibility() {
    return LuaDocPsiImplUtilKt.getVisibility(this);
  }

  @Override
  public int getWorth() {
    return LuaDocPsiImplUtilKt.getWorth(this);
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    return LuaDocPsiImplUtilKt.getNameIdentifier(this);
  }

  @Override
  public @NotNull PsiElement setName(@NotNull String newName) {
    return LuaDocPsiImplUtilKt.setName(this, newName);
  }

  @Override
  public @Nullable String getName() {
    return LuaDocPsiImplUtilKt.getName(this);
  }

  @Override
  public int getTextOffset() {
    return LuaDocPsiImplUtilKt.getTextOffset(this);
  }

  @Override
  public @Nullable String getFieldName() {
    return LuaDocPsiImplUtilKt.getFieldName(this);
  }

  @Override
  public @NotNull String toString() {
    return LuaDocPsiImplUtilKt.toString(this);
  }

  @Override
  public @NotNull ItemPresentation getPresentation() {
    return LuaDocPsiImplUtilKt.getPresentation(this);
  }

  @Override
  public boolean isDeprecated() {
    return LuaDocPsiImplUtilKt.isDeprecated(this);
  }

}
