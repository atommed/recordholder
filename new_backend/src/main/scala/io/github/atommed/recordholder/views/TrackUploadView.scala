package io.github.atommed.recordholder.views

import java.io.File
import java.nio.file.{CopyOption, Files, Path, Paths, StandardCopyOption}
import javax.ejb.EJB
import javax.faces.bean.{ManagedBean, ViewScoped}

import io.github.atommed.recordholder.beans.TrackAnalyzerBean
import org.primefaces.model.UploadedFile

import scala.beans.BeanProperty

/**
  * Created by gregory on 17.11.16.
  */
@ManagedBean
@ViewScoped
class TrackUploadView {
  @EJB var analyzer: TrackAnalyzerBean = _
  @BeanProperty var file: UploadedFile = _

  def upload() = {
    val newFile = File.createTempFile("track", null)
    Files.copy(file.getInputstream, newFile.toPath, StandardCopyOption.REPLACE_EXISTING)
    val res = analyzer.getMetaData(newFile)
  }
}
