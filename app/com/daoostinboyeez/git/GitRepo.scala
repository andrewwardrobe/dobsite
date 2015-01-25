package com.daoostinboyeez.git

import java.io.{FileWriter, File}

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk

/**
 * Created by andrew on 25/01/15.
 */
object GitRepo {
  val gitDir = new File("content/.git")
  val repo = new FileRepository(gitDir)
  val git = new Git(repo)

  def getBranch = { repo.getFullBranch()}

  def newFile(path:String, fileData:String){
    val newFile = new File(repo.getDirectory.getParent,path)
    val writer = new FileWriter(newFile)
    writer.append(fileData)
    writer.close()
    git.add().addFilepattern(path).call()
    git.commit().setMessage("Added File $path").call()
  }

  def updateFile(path:String, fileData:String){
    val newFile = new File(repo.getDirectory.getParent,path)
    val writer = new FileWriter(newFile)
    writer.append(fileData)
    writer.close()
    git.add().addFilepattern(path).call()
    git.commit().setMessage("Edited File $path").call()
  }

  def getFile(path:String) : String = {
    getFile(path,Constants.HEAD)
  }


  def getFile(path:String,revSpec:String)= {
    val id = repo.resolve(revSpec)
    val reader = repo.newObjectReader()
    val revWalk = new RevWalk(reader)
    val commit = revWalk.parseCommit(id)

    val revTree = commit.getTree()
    val treeWalk = TreeWalk.forPath(reader,path,revTree)

    if(treeWalk != null){
      val data = reader.open(treeWalk.getObjectId(0)).getBytes()
      reader.release()
      new String(data,"utf-8")
    }
    else{
      reader.release()
      new String("")
    }
  }
}
