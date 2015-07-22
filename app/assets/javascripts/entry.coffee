require ['common'], (common) ->
  require ['highlight.pack'], ()->
    $("#codeBox").each (i, block) ->
      hljs.highlightBlock(block)
