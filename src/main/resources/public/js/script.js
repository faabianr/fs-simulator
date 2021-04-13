$( document ).ready( function() {

    var prompt_id=0;
    var cmdline = 0;

    function init() {
      // Init first command line
         $("#cmdline0").keypress(function (ev) {
           var keycode = (ev.keyCode ? ev.keyCode : ev.which);
                if (keycode == '13') {
                  prompt_id++;
                  cmdline++;
                  var html = executeCommand($("#cmdline0").val());
                  alert("Comando enviado:" + html);
                  // When the user press enter it creates a new line in prompt
                  writeNewLine();
                }
           });
    }

    //Execute every time the user press enter
    function writeNewLine(){
        // Create a clone of the terminal prompt line and put a new id
        $("#terminal_prompt").clone().appendTo("#terminal_body").each(function(){
           $(this).attr("class", "prompt"+(prompt_id));
           var newcmdLine = $(this).find("input")
           newcmdLine.attr("id", "cmdline" + cmdline);
           newcmdLine.val("");
           newcmdLine.focus();
           newcmdLine.keypress(function (ev) {
                var keycode = (ev.keyCode ? ev.keyCode : ev.which);
                 if (keycode == '13') {
                 var commandValue =  newcmdLine.val();
                    prompt_id++;
                    cmdline++;
                    var html = executeCommand(commandValue);
                    alert("Comando enviado:" + html);
                    writeNewLine();
                 }
           });
      });
    }

      function executeCommand(command){
          var executionRequest = {
               "input": command
          };
          
          $.ajax({
                url: 'filesystem/commands',
                dataType: 'json',
                data: JSON.stringify(executionRequest),
                type : 'POST',
                contentType: 'application/json',
                Accept: 'application/json',
                cache : false,
                success: function(resultData) { alert('Command read') }
          });
          return success;
      }
  init();
});