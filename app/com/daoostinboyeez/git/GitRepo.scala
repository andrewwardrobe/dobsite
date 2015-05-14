package com.daoostinboyeez.git

import java.io.{File, FileWriter}
import java.util.Date

import com.sun.xml.internal.bind.v2.TODO
import models.{ContentMeta}
import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.{Repository, Constants}
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk
import play.api.{Logger, Play}
import play.api.Play.current
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.apache.commons.io



/**
 * Created by andrew on 25/01/15.
 */
class GitRepo {

  val repoLocation = Play.application.configuration.getString("git.repo.dir").getOrElse("content/.git")
  val repo = init
  val git = new Git(repo)

  def find = {
    val commits = git.log().call()
    val commitList: ListBuffer[String] = new ListBuffer[String]()
    commits.foreach{ commit =>
      commitList += commit.getName()
    }
    commitList.toList
  }

  def findWithDate(path:String) = {
    val commits = git.log().addPath(path).call()
    val commitList: ListBuffer[ContentMeta] = new ListBuffer[ContentMeta]()
    commits.foreach{ commit =>
      commitList +=new ContentMeta(commit.getName , commit.getAuthorIdent.getWhen().toString)
    }
    commitList.toList
  }

  def findWithDate = {
    val commits = git.log().call()
    val commitList: ListBuffer[ContentMeta] = new ListBuffer[ContentMeta]()
    commits.foreach{ commit =>
      commitList +=new ContentMeta(commit.getName , commit.getAuthorIdent.getWhen().toString)
    }
    commitList.toList
  }


  def findRevDates = {
    val commits = git.log().call()
    val commitList: ListBuffer[String] = new ListBuffer[String]()
    commits.foreach{ commit =>
      commitList += commit.getAuthorIdent.getWhen().toString
    }
    commitList.toList
  }

  def find(path:String) = {
    val commits = git.log().addPath(path).call()
    val commitList: ListBuffer[String] = new ListBuffer[String]()
    commits.foreach{ commit =>
      commitList += commit.getName()
    }
    commitList.toList
  }

  def findRevDates(path:String) = {
    val commits = git.log().addPath(path).call()
    val commitList: ListBuffer[String] = new ListBuffer[String]()
    commits.foreach{ commit =>
      commitList += commit.getAuthorIdent.getWhen().toString
    }
    commitList.toList
  }

  def getRepoDir = git.getRepository.getDirectory

  def getBranch = { git.getRepository.getFullBranch()}

  def newFile(path:String, fileData:String)={
    doFile(path, fileData,s"Added File $path")
  }

  def newFile(path:String, fileData:String, commitMsg:String)={
    doFile(path, fileData,commitMsg)
  }

  def init = {
   Play.application.configuration.getString("git.repo.testmode").getOrElse("false") match {
     case "false" =>
       Logger.info("In Non Test Mode = ")
       new FileRepository(repoLocation)
     case "true" =>
       val gitDir = repoLocation.substring(0,repoLocation.lastIndexOf("/.git"))
       val repoDir = new File(gitDir)
       repoDir.deleteOnExit()
       Git.init().setDirectory(repoDir).call()
       val repoFile = new File(repoLocation)
       repoFile.deleteOnExit()
       FileRepositoryBuilder.create(repoFile)
   }

 }

  def refresh = {
    Play.application.configuration.getString("git.repo.testmode").getOrElse("false") match {
      case "true" =>
        val gitDir = repoLocation.substring (0, repoLocation.lastIndexOf ("/.git") )
        val repoDir = new File (gitDir)
        FileUtils.deleteDirectory(repoDir)
        repoDir.deleteOnExit ()
        Git.init ().setDirectory (repoDir).call ()
        val repoFile = new File (repoLocation)
        repoFile.deleteOnExit ()
        FileRepositoryBuilder.create (repoFile)
        createFile("", "Test File")
    }
  }

  def updateFile(path:String, fileData:String)={
    doFile(path, fileData,s"Edited File $path")
  }

  def updateFile(path:String, fileData:String, commitMsg :String)={
    doFile(path, fileData,commitMsg)
  }


  def createFile(prefix:String, fileData:String, commitMsg:String) = {
    val path = prefix + genFileName
    doFile(path, fileData,commitMsg)
    path
  }

  def createFile(fileData:String, commitMsg:String):String = {
    createFile("",fileData,commitMsg)
  }

  def createFile(fileData:String):String = {
    createFile("",fileData,"")
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

object GitRepo{
  lazy val repo = init

  private def init = {
    new GitRepo
  }

  def apply() = {repo}
  def refresh = repo.refresh
}
