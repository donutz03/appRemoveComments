package com.removecomments.appremovecomments

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaRecursiveElementVisitor
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager

class RemoveCommentsAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project? = event.project

        val editor: Editor? = FileEditorManager.getInstance(project!!).selectedTextEditor
        if (editor == null) {
            println("No active editor found.")
            return
        }

        val document = editor.document
        val virtualFile = FileDocumentManager.getInstance().getFile(document)
        if (virtualFile == null) {
            println("No active file found.")
            return
        }

        val psiFile: PsiFile? = PsiManager.getInstance(project).findFile(virtualFile)
        if (psiFile == null || !psiFile.name.endsWith(".java")) {
            println("The current file is not a valid Java file.")
            return
        }

        val commentsToDelete = mutableListOf<PsiComment>()
        psiFile.accept(object : JavaRecursiveElementVisitor() {
            override fun visitComment(comment: PsiComment) {
                super.visitComment(comment)
                commentsToDelete.add(comment)
            }
        })

        WriteCommandAction.runWriteCommandAction(project) {
            commentsToDelete.forEach { it.delete() }
        }

        println("All comments removed from the current file.")
    }
}
