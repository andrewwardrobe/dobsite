package com.daoostinboyeez.git

import java.io.{File, FileWriter}
import java.util.Date

import com.sun.xml.internal.bind.v2.TODO
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.{Repository, Constants}
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import play.api.{Logger, Play}
import play.api.Play.current



/**
 * Created by andrew on 25/01/15.
 */
object GitRepo {
  val repoLocation = Play.application.configuration.getString("git.repo.dir").getOrElse("content/.git")


  val repo = init

  Logger.info("Repo Location = "  +  Play.application.configuration.getString("git.repo.dir").get)

  val git = new Git(repo)


  def getRepoDir = git.getRepository.getDirectory
  def getBranch = { git.getRepository.getFullBranch()}

  def newFile(path:String, fileData:String)={
    doFile(path, fileData,s"Added File $path")
  }

 def init = {
   Play.application.configuration.getString("git.repo.testmode").getOrElse("false") match {
     case "false" =>
       Logger.info("In Non Test Mode = ")
       new FileRepository(repoLocation)
     case "true" =>
       val gitDir = repoLocation.substring(0,repoLocation.lastIndexOf("/.git"))
       Logger.info("Git Dir = " +gitDir)
       val repoDir = new File(gitDir)
       repoDir.deleteOnExit()
       Git.init().setDirectory(repoDir).call()
       val repoFile = new File(repoLocation)
       repoFile.deleteOnExit()
       FileRepositoryBuilder.create(repoFile)
   }

 }

  def updateFile(path:String, fileData:String)={
    doFile(path, fileData,s"Edited File $path")
  }

  def createFile(prefix:String, fileData:String) = {
    val path = prefix + genFileName
    doFile(path, fileData,s"Created file $path")
    path
  }

  def createFile(fileData:String):String = {
    createFile("",fileData)
  }

  def genFileName = {
    val date = new Date()
    new String(""+date.getTime())
  }

  def doFile(path:String, fileData:String, commitMsg: String){
    val newFile = new File(repo.getDirectory.getParent,path)
    val writer = new FileWriter(newFile)
    writer.append(fileData)
    writer.close()
    git.add().addFilepattern(path).call()
    git.commit().setMessage(commitMsg).call()
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
