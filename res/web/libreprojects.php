<?php

function startHtml()
{
    print <<<END
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
    <head>
        <meta charset="utf-8">
        <meta  http-equiv="content-type" content="text/html; charset=UTF-8" />
        <link rel="stylesheet" type="text/css" href="style.css">
        <title>Libre project</title>
    </head>
    <body>
END;
}

function endHtml()
{
    print("    </body>");
    print("</html>");
}

function ask_user_for_parameters()
{
    startHtml();
?>

        <div id="header">
            <h1>Libre Projects</h1>
        </div>
        <div id="content">
        <form method="POST" action="<?php echo $_SERVER['SCRIPT_NAME'] ?>" >
            <p> Project name :
                <input type="text" name="project_name" size=30 value="blinky" />
            </p>

<h2>Environment</h2>

            <p> Select your environment:
              <fieldset>
                <p>
                <input type="radio" id="wiki" name="environment" value="asReference" checked="checked" >
                <label for="wiki"> from Wiki</label>
                <input type="text" name="env_wiki" size=30 value="arduino2560" />
                name one from the <a href=https://wiki.ing-poetter.de/doku.php?id=devices:devices_overview>full list of supported environments</a>.
                </p>
                <p>
                <input type="radio" id="user" name="environment" value="userDefined">
                <label for="user"> self defined</label>
<textarea name="env_user" cols="80" rows="10">
&lt;environment>
    &lt;tool name="avr/atmega2560" />
    &lt;root_api name="program_entry_point" />
    &lt;resources>
        &lt;user_led algorithm="gpio" port="B" pin="7" />
    &lt;/resources>
&lt;/environment>
</textarea>
                </p>
              </fieldset>
            </p>

<h2>Solution</h2>

            <p> Select your solution:
              <fieldset>
                <p>
                <input type="radio" id="wiki" name="solution" value="asReference" checked="checked" >
                <label for="wiki"> from Wiki</label>
                <input type="text" name="sol_wiki" size=30 value="blinky" />
                name one from the <a href=https://wiki.ing-poetter.de/doku.php?id=solutions:solutions_overview>full list of supported solutions</a>.
                </p>
                <p>
                <input type="radio" id="user" name="solution" value="userDefined">
                <label for="user"> self defined</label>
<textarea name="sol_user" cols="80" rows="10">
&lt;solution>
  &lt;os algorithm="super_loop">
    &lt;user_led />
    &lt;blink algorithm="pwm_task" frequency_Hz="1" dutyCycle_percent="60" >
      &lt;user_led />
    &lt;/blink>
  &lt;/os>
&lt;/solution>
</textarea>
                </p>
              </fieldset>
            </p>
<h2>fine tuning</h2>
            <p>
              <input type="checkbox" id="docCode" name="docCode" value="docCode">
              <label for="docCode">document the use of algorithms.</label>
              <input type="checkbox" id="splitMakefile" name="splitMakefile" value="splitMakefile">
              <label for="splitMakefile">split makefile into several files.</label>
            </p>
            <p>
              <input type="checkbox" id="debug" name="debug" value="debug">
              <label for="docCode">do not generate anything. Just show what would happen.</label>
            </p>
            <input type="submit" name="download" value="create project" />
            <input type="hidden" name="hasData" value ="1" />
        </form>
        </div>
<?php
    endHtml();
}

function generate_project()
{
    startHtml();
    echo '<div id="content">';
    $descriptorspec = array(
       0 => array("pipe", "r"),  // STDIN is a pipe, that the child reads
       1 => array("pipe", "w"),  // STDOUT is a pipe, that the child writes to
       2 => array("pipe", "w")   // STDERR is a pipe, that the child writes to
    );

    $regex = "/^\w+$/";
    $projectName = $_POST['project_name'] ;
    if(empty($projectName))
    {
        $projectName = "blinky";
    }
    else if (!preg_match($regex, $projectName))
    {
        $projectName = "blinky";
    }
    echo "<br /> Project name : " . $projectName . "<br />";

    $addPara = '-v -v ';
    if(array_key_exists("docCode", $_POST))
    {
        $addPara = $addPara . "-Ddocument_code_source=true ";
    }
    if(array_key_exists("splitMakefile", $_POST))
    {
        $addPara = $addPara . "-Dsplit_makefile=true ";
    }

    $solText = "";
    $InStream = "";
    if("asReference" == $_POST['solution'])
    {
        $solText = "    <solution ref='" . $_POST['sol_wiki'] . "' />\n";
    }
    else
    {
        $solText = $_POST['sol_user'];
    }

    if("asReference" == $_POST['environment'])
    {
        $InStream =  "<?xml version='1.0' encoding='utf-8' ?>\n";
        $InStream = $InStream . "<project>\n";
        $InStream = $InStream . "    <environment ref='" . $_POST['env_wiki'] . "' />\n";
        $InStream = $InStream . $solText;
        $InStream = $InStream . "</project>\n";
    }
    else
    {
        $InStream = "<?xml version='1.0' encoding='utf-8' ?>\n";
        $InStream = $InStream . "<project>\n";
        $InStream = $InStream . $_POST['env_user'];
        $InStream = $InStream . $solText;
        $InStream = $InStream . "</project>\n";
    }

    include '../puzzler/cfg.inc';

    $cmd = 'java -jar ' . $jarLocation . ' -x ' . $wikiUrl . ' --zip_to_stdout --prj_name ' . $projectName . ' -l lib/ -s solutions/ -p /home/lars/public_html/ -e devices/ ' . $addPara;
    if(array_key_exists("debug", $_POST))
    {
        echo "command line : " . $cmd . "<br/>\n";
        $InStream = str_replace("<", "&lt;", $InStream);
        echo "In Stream : <pre><code>" . $InStream . "</code></pre> <br />\n";

        $post_str = print_r($_POST,$return = true);
        $post_str = str_replace("<", "&lt;", $post_str);
        echo "POST Data : <pre><code>" .$post_str . "</code></pre> <br />\n";
        echo "</div>";
        endHtml();
    }
    else
    {
      $process = proc_open($cmd, $descriptorspec, $pipes);

      if (is_resource($process)) {
        // $pipes is now
        // 0 => writehandle, child STDIN
        // 1 => readhandle, child STDOUT
        // 2 => readhandle, child STDERR
        $solText = "";
        if("asReference" == $_POST['solution'])
        {
            $solText = "    <solution ref='" . $_POST['sol_wiki'] . "' />\n";
        }
        else
        {
            $solText = $_POST['sol_user'];
        }

        if("asReference" == $_POST['environment'])
        {
            fwrite($pipes[0], "<?xml version='1.0' encoding='utf-8' ?>\n");
            fwrite($pipes[0], "<project>\n");
            fwrite($pipes[0], "    <environment ref='" . $_POST['env_wiki'] . "' />\n");
            fwrite($pipes[0], $solText);
            fwrite($pipes[0], "</project>\n");
        }
        else
        {
            fwrite($pipes[0], "<?xml version='1.0' encoding='utf-8' ?>\n");
            fwrite($pipes[0], "<project>\n");
            fwrite($pipes[0], $_POST['env_user']);
            fwrite($pipes[0], $solText);
            fwrite($pipes[0], "</project>\n");
        }
        fwrite($pipes[0], "<<<EOF>>>\n\n");
        fclose($pipes[0]);

        stream_set_blocking($pipes[2], false);
        stream_set_timeout($pipes[2],0);
        stream_set_blocking($pipes[1], false);
        stream_set_timeout($pipes[1],0);

        $content_stdout = "";
        $content_stderr = "";
        $done = false;
        $l_err = 0;
        $l_out = 0;
        $l_total = 0;
        do
        {
            sleep(2);
            $data = "";
            do {
                $chunk = fread($pipes[2], 4096);
                $data = $data . $chunk;
            } while(0 < strlen($chunk));
            $l_err = strlen($data);
            if(0 != $l_err)
            {
                $l_total += $l_err;
                echo "<br/> log : + " . strlen($data) . " bytes !<br/>\n";
            }
            $content_stderr = $content_stderr . $data;

            $data = "";
            do {
                $chunk = fread($pipes[1], 4096);
                $data = $data . $chunk;
            } while(0 < strlen($chunk));
            $l_out = strlen($data);
            if( 0 != $l_out)
            {
                $l_total += $l_out;
                echo "<br/> project : + " . strlen($data) . " bytes !<br/>\n";
            }
            $content_stdout = $content_stdout . $data;
            if((0 == $l_err) && (0 == $l_out) && (0 != $l_total))
            {
                $done = true;
            }
            flush();
            ob_flush();
        } while(false == $done);

        fclose($pipes[2]);
        fclose($pipes[1]);
        $return_value = proc_close($process);


        if(0 == $return_value)
        {
            // OK
            // startHtml();
            echo "<br/> OK: project has been created !<br/>\n";
            echo "<br/> project : " . strlen($content_stdout) . " bytes!<br/>\n";
            echo '<form method="POST" action="' . $_SERVER['SCRIPT_NAME'] .'" >';
            echo '  <input type="submit" name="download" value="download project" />';
            echo '  <input type="hidden" name="project_name" value ="' . htmlspecialchars($projectName) . '" />';
            echo '  <input type="hidden" name="zip" value ="' . bin2hex($content_stdout) . '" />';
            echo '</form>';
            echo "<br/> log : " . strlen($content_stderr) . " bytes!<br/>\n";
            echo '<form method="POST" action="' . $_SERVER['SCRIPT_NAME'] .'" >';
            echo '  <input type="submit" name="download" value="download log" />';
            echo '  <input type="hidden" name="stderr" value ="' . htmlspecialchars($content_stderr) . '" />';
            echo '</form>';
            echo '<form method="POST" action="' . $_SERVER['SCRIPT_NAME'] .'" >';
            echo '  <input type="submit" name="create different project" value="create different project" />';
            echo '</form>';
            echo "</div>";
            endHtml();
        }
        else
        {
            // failed
            // startHtml();
            echo "<br/>ERROR ($return_value) Failed to create project !<br/>";
            echo "<br/><br/>";
            echo "command line : " . $cmd . "<br/>";
            echo "<br/><br/>";
            echo "POST Data : ";
            print_r($_POST);
            echo "<br/><br/>";
            $msg = str_replace("<", "&lt;", $content_stderr);
            echo "<pre><code>$msg</code></pre>";
            echo '<form method="POST" action="' . $_SERVER['SCRIPT_NAME'] .'" >';
            echo '  <input type="submit" name="create different project" value="create different project" />';
            echo '</form>';
            echo "</div>";
            endHtml();
        }
      }
   }
}

function download_log()
{
    startHtml();
    //echo "POST Data : ";
    //print_r($_POST);
    $msg = str_replace("<", "&lt;", $_POST['stderr']);
    echo "<pre><code>$msg</code></pre>";
    endHtml();
}

function download_zip()
{
    $zip_content = hex2bin($_POST['zip']);
    $file_name = $_POST['project_name'] . ".zip";
    header('Content-Description: File Transfer');
    header("Content-Type: application/application/zip");
    header("Content-Disposition: attachment; filename=\"$file_name\"");
    header('Expires: 0');
    header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
    header('Content-Length: ' . strlen($zip_content));
    print($zip_content);
}


if( array_key_exists('hasData', $_POST))
{
    generate_project();
}
else if( array_key_exists('stderr', $_POST))
{
    download_log();
}
else if( array_key_exists('zip', $_POST))
{
    download_zip();
}
else
{
    ask_user_for_parameters();
}
?>
