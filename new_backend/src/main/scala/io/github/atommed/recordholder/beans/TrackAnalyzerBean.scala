package io.github.atommed.recordholder.beans

import java.io.File
import java.nio.file.{Files, Path, Paths}
import javax.annotation.{PostConstruct, Resource}
import javax.ejb.Stateless
import javax.inject.Inject

import io.github.atommed.recordholder.util.MetadataRetriever


/**
  * Created by gregory on 17.11.16.
  */

@Stateless
class TrackAnalyzerBean {
  @Resource(lookup="config/paths/analyzer-executable") var executablePath: String = _
  var analyzer: MetadataRetriever = _
  var workDir: Path = _

  @PostConstruct
  def init() = {
    workDir = Files.createTempDirectory("metadata_analyzer-wd")
    analyzer = new MetadataRetriever(Paths.get(executablePath))
  }
}
