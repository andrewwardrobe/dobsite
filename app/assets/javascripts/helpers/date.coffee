define () ->

  @pad2 = (n) ->
# always returns a string
    (if n < 10 then '0' else '') + n
  
  Date::yyyymmdd = ->
      @getFullYear() + pad2(@getMonth() + 1) + pad2(@getDate()) + pad2(@getHours()) + pad2(@getMinutes()) + pad2(@getSeconds())
  Date::DTString  = ->
    "#{pad2(@getDate())}/#{pad2(@getMonth() + 1)}/#{pad2(@getFullYear())} #{pad2(@getHours())}:#{pad2(@getMinutes())}:#{pad2(@getSeconds())}"